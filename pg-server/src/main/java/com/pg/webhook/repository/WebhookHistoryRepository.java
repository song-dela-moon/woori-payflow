package com.pg.webhook.repository;

import com.pg.webhook.entity.WebhookHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookHistoryRepository extends JpaRepository<WebhookHistory, Long> {
}