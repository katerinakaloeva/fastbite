package com.fastbite.restaurantservice.restaurant.mapper;

import com.fastbite.restaurantservice.restaurant.dto.CreateMenuItemRequest;
import com.fastbite.restaurantservice.restaurant.dto.MenuItemResponse;
import com.fastbite.restaurantservice.restaurant.dto.UpdateMenuItemRequest;
import com.fastbite.restaurantservice.restaurant.model.MenuCategory;
import com.fastbite.restaurantservice.restaurant.model.MenuItem;
import org.springframework.stereotype.Component;

@Component
public class MenuItemMapper {

    public MenuItem toEntity(CreateMenuItemRequest request, MenuCategory category) {
        return MenuItem.builder()
                .category(category)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .currency(request.currency())
                .build();
    }

    public void updateEntity(UpdateMenuItemRequest request, MenuItem menuItem) {
        menuItem.updateDetails(
                request.name(),
                request.description(),
                request.price(),
                request.currency(),
                request.available()
        );
    }

    public MenuItemResponse toResponse(MenuItem menuItem) {
        return new MenuItemResponse(
                menuItem.getId(),
                menuItem.getCategory().getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getCurrency(),
                menuItem.isAvailable(),
                menuItem.getCreatedAt(),
                menuItem.getUpdatedAt()
        );
    }
}
