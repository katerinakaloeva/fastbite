package com.fastbite.orderservice.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID menuItemId,
        String itemName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
}
