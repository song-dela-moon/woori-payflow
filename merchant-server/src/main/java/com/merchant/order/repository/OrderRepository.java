package com.merchant.order.repository;

import com.merchant.order.entity.MerchantOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<MerchantOrder, Long> {

    Optional<MerchantOrder> findByOrderUid(String orderUid);
}
