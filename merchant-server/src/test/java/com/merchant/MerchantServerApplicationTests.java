package com.merchant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merchant.order.repository.OrderRepository;
import com.merchant.payment.client.PgClient;
import com.merchant.payment.dto.PgPaymentResponse;
import com.merchant.payment.repository.MerchantPaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MerchantServerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MerchantPaymentRepository merchantPaymentRepository;

    @MockitoBean
    private PgClient pgClient;

    @Test
    void contextLoads() {
    }

    @Test
    void 주문을_생성하고_조회할_수_있다() throws Exception {
        merchantPaymentRepository.deleteAll();
        orderRepository.deleteAll();

        String createRequest = """
                {
                  "productName": "텀블러",
                  "amount": 15000
                }
                """;

        String createResponse = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName").value("텀블러"))
                .andExpect(jsonPath("$.amount").value(15000))
                .andExpect(jsonPath("$.orderStatus").value("CREATED"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode createdOrder = objectMapper.readTree(createResponse);
        String orderUid = createdOrder.get("orderUid").asText();

        mockMvc.perform(get("/orders/{orderUid}", orderUid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderUid").value(orderUid))
                .andExpect(jsonPath("$.productName").value("텀블러"))
                .andExpect(jsonPath("$.amount").value(15000))
                .andExpect(jsonPath("$.orderStatus").value("CREATED"));
    }

    @Test
    void 결제_승인시_결제와_주문상태가_완료로_반영된다() throws Exception {
        merchantPaymentRepository.deleteAll();
        orderRepository.deleteAll();

        String orderUid = createOrder("노트북 거치대", 32000L);

        PgPaymentResponse approvedResponse = new PgPaymentResponseFixture(
                "PG-APPROVED-001",
                "APPROVED",
                "AP-123456",
                null,
                null
        ).toResponse();
        given(pgClient.requestPaymentApproval(any())).willReturn(approvedResponse);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderUid": "%s",
                                  "cardNumber": "1234567812345678",
                                  "expiryYear": "28",
                                  "expiryMonth": "12",
                                  "birthOrBizNo": "900101",
                                  "cardPassword2Digits": "12",
                                  "installmentMonths": 0
                                }
                                """.formatted(orderUid)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderUid").value(orderUid))
                .andExpect(jsonPath("$.pgPaymentId").value("PG-APPROVED-001"))
                .andExpect(jsonPath("$.paymentStatus").value("APPROVED"))
                .andExpect(jsonPath("$.approvalCode").value("AP-123456"));

        mockMvc.perform(get("/orders/{orderUid}", orderUid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("PAYMENT_COMPLETED"));
    }

    @Test
    void 결제_실패시_결제와_주문상태가_실패로_반영된다() throws Exception {
        merchantPaymentRepository.deleteAll();
        orderRepository.deleteAll();

        String orderUid = createOrder("무선 마우스", 54000L);

        PgPaymentResponse failedResponse = new PgPaymentResponseFixture(
                "PG-FAILED-001",
                "FAILED",
                null,
                "CARD_DECLINED",
                "한도 초과"
        ).toResponse();
        given(pgClient.requestPaymentApproval(any())).willReturn(failedResponse);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderUid": "%s",
                                  "cardNumber": "1234567812345678",
                                  "expiryYear": "28",
                                  "expiryMonth": "12",
                                  "birthOrBizNo": "900101",
                                  "cardPassword2Digits": "12",
                                  "installmentMonths": 3
                                }
                                """.formatted(orderUid)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderUid").value(orderUid))
                .andExpect(jsonPath("$.pgPaymentId").value("PG-FAILED-001"))
                .andExpect(jsonPath("$.paymentStatus").value("FAILED"))
                .andExpect(jsonPath("$.failureCode").value("CARD_DECLINED"))
                .andExpect(jsonPath("$.failureMessage").value("한도 초과"));

        mockMvc.perform(get("/orders/{orderUid}", orderUid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("PAYMENT_FAILED"));
    }

    private String createOrder(String productName, long amount) throws Exception {
        String response = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productName": "%s",
                                  "amount": %d
                                }
                                """.formatted(productName, amount)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("orderUid").asText();
    }

    private record PgPaymentResponseFixture(
            String paymentId,
            String status,
            String approvalCode,
            String failureCode,
            String failureMessage
    ) {
        private PgPaymentResponse toResponse() throws Exception {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue("""
                    {
                      "paymentId": "%s",
                      "status": "%s",
                      "approvalCode": %s,
                      "failureCode": %s,
                      "failureMessage": %s
                    }
                    """.formatted(
                    paymentId,
                    status,
                    nullableJsonValue(approvalCode),
                    nullableJsonValue(failureCode),
                    nullableJsonValue(failureMessage)
            ), PgPaymentResponse.class);
        }

        private String nullableJsonValue(String value) {
            return value == null ? "null" : "\"%s\"".formatted(value);
        }
    }

}
