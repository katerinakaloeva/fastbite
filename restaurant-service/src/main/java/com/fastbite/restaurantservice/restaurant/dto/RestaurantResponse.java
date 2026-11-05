package com.fastbite.restaurantservice.restaurant.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RestaurantResponse(
        UUID id,
        String name,
        String address,
        boolean active,
        String description,
        String phone,
        String city,
        String postalCode,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}