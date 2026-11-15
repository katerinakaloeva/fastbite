package com.fastbite.restaurantservice.restaurant.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PublicMenuItemResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        String currency
) {
}