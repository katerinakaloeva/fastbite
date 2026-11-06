package com.fastbite.restaurantservice.restaurant.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MenuCategoryResponse(
        UUID id,
        UUID restaurantId,
        String name,
        int displayOrder,
        boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}