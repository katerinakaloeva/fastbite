package com.fastbite.restaurantservice.restaurant.dto;

import java.util.List;

public record PublicRestaurantPageResponse(
        List<PublicRestaurantSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
