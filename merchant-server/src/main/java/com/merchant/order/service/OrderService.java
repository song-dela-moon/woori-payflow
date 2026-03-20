package com.merchant.order.service;

import com.merchant.order.dto.OrderCreateRequest;
import com.merchant.order.dto.OrderResponse;
import com.merchant.order.entity.MerchantOrder;
import com.merchant.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        String orderUid = generateOrderUid();

        MerchantOrder order = MerchantOrder.create(
                orderUid,
                request.getProductName(),
                request.getAmount()
        );

        MerchantOrder savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }

    public OrderResponse getOrder(String orderUid) {
        MerchantOrder order = orderRepository.findByOrderUid(orderUid)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다. orderUid=" + orderUid));

        return OrderResponse.from(order);
    }

    private String generateOrderUid() {
        return "ORDER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}