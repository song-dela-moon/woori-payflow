package com.pg.webhook.entity;

import com.pg.merchant.entity.Merchant;
import com.pg.payment.entity.Payment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "webhook_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WebhookHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    @Column(name = "event_type", nullable = false, length = 30)
    private String eventType;

    @Column(name = "callback_url", nullable = false, length = 255)
    private String callbackUrl;

    @Lob
    @Column(name = "request_body", nullable = false)
    private String requestBody;

    @Column(name = "response_status")
    private Integer responseStatus;

    @Column(name = "success", nullable = false)
    private boolean success;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Builder
    public WebhookHistory(
            Payment payment,
            Merchant merchant,
            String eventType,
            String callbackUrl,
            String requestBody,
            Integer responseStatus,
            boolean success,
            LocalDateTime sentAt
    ) {
        this.payment = payment;
        this.merchant = merchant;
        this.eventType = eventType;
        this.callbackUrl = callbackUrl;
        this.requestBody = requestBody;
        this.responseStatus = responseStatus;
        this.success = success;
        this.sentAt = sentAt;
    }
}