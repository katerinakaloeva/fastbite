package com.fastbite.restaurantservice.restaurant.dto;

import java.util.List;
import java.util.UUID;

public record PublicMenuCategoryResponse(
        UUID id,
        String name,
        int displayOrder,
        List<PublicMenuItemResponse> items
) {
}