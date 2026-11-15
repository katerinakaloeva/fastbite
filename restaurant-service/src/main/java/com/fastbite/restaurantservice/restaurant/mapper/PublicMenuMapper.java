package com.fastbite.restaurantservice.restaurant.mapper;

import com.fastbite.restaurantservice.restaurant.dto.PublicMenuCategoryResponse;
import com.fastbite.restaurantservice.restaurant.dto.PublicMenuItemResponse;
import com.fastbite.restaurantservice.restaurant.dto.PublicMenuResponse;
import com.fastbite.restaurantservice.restaurant.model.MenuCategory;
import com.fastbite.restaurantservice.restaurant.model.MenuItem;
import com.fastbite.restaurantservice.restaurant.model.Restaurant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class PublicMenuMapper {

    public PublicMenuResponse toResponse(
            Restaurant restaurant,
            List<MenuCategory> categories,
            Map<UUID, List<MenuItem>> itemsByCategory
    ) {
        List<PublicMenuCategoryResponse> categoryResponses = categories
                .stream()
                .map(category -> toCategoryResponse(
                        category,
                        itemsByCategory.getOrDefault(category.getId(), List.of())
                ))
                .toList();

        return new PublicMenuResponse(
                restaurant.getId(),
                restaurant.getName(),
                categoryResponses
        );
    }

    private PublicMenuCategoryResponse toCategoryResponse(
            MenuCategory category,
            List<MenuItem> items
    ) {
        return new PublicMenuCategoryResponse(
                category.getId(),
                category.getName(),
                category.getDisplayOrder(),
                items.stream()
                        .map(this::toItemResponse)
                        .toList()
        );
    }

    private PublicMenuItemResponse toItemResponse(MenuItem item) {
        return new PublicMenuItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getCurrency()
        );
    }
}