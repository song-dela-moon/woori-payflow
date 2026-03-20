package com.merchant.order.controller;

import com.merchant.order.dto.OrderCreateRequest;
import com.merchant.order.dto.OrderResponse;
import com.merchant.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody OrderCreateRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/{orderUid}")
    public OrderResponse getOrder(@PathVariable String orderUid) {
        return orderService.getOrder(orderUid);
    }
}