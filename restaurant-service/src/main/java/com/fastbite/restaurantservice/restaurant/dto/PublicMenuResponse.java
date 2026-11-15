package com.fastbite.restaurantservice.restaurant.dto;

import java.util.List;
import java.util.UUID;

public record PublicMenuResponse(
        UUID restaurantId,
        String restaurantName,
        List<PublicMenuCategoryResponse> categories
) {
}