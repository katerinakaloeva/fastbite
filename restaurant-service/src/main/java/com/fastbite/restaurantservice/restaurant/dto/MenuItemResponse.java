package com.fastbite.restaurantservice.restaurant.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record MenuItemResponse(
        UUID id,
        UUID categoryId,
        String name,
        String description,
        BigDecimal price,
        String currency,
        boolean available,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
