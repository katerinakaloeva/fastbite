package com.fastbite.orderservice.order.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

// Local representation of the restaurant-service public menu API.
public record RestaurantMenuResponse(
        UUID restaurantId,
        String restaurantName,
        List<Category> categories
) {
    public record Category(UUID id, String name, int displayOrder, List<Item> items) {
    }

    public record Item(UUID id, String name, String description, BigDecimal price, String currency) {
    }
}