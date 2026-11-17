package com.fastbite.restaurantservice.restaurant.dto;

import java.util.UUID;

public record PublicRestaurantDetailsResponse(
        UUID id,
        String name,
        String description,
        String address,
        String phone,
        String city,
        String municipality,
        String region,
        String postalCode
) {
}
