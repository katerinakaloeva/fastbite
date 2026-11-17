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
        String municipality,
        String region,
        String postalCode,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public RestaurantResponse(
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
        this(
                id,
                name,
                address,
                active,
                description,
                phone,
                city,
                null,
                null,
                postalCode,
                createdAt,
                updatedAt
        );
    }
}
