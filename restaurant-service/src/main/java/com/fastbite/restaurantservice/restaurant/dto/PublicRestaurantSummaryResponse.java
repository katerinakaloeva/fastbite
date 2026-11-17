package com.fastbite.restaurantservice.restaurant.dto;

import java.util.UUID;

public record PublicRestaurantSummaryResponse(
        UUID id,
        String name,
        String description,
        String city,
        String municipality,
        String region
) {
}
