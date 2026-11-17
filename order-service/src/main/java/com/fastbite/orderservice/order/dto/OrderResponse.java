package com.fastbite.orderservice.order.dto;

import com.fastbite.orderservice.order.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID restaurantId,
        OrderStatus status,
        List<OrderItemResponse> items,
        BigDecimal totalAmount,
        String currency
) {
}
