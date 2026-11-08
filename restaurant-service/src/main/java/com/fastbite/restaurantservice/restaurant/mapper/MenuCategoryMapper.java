package com.fastbite.restaurantservice.restaurant.mapper;

import com.fastbite.restaurantservice.restaurant.dto.CreateMenuCategoryRequest;
import com.fastbite.restaurantservice.restaurant.dto.MenuCategoryResponse;
import com.fastbite.restaurantservice.restaurant.dto.UpdateMenuCategoryRequest;
import com.fastbite.restaurantservice.restaurant.model.MenuCategory;
import com.fastbite.restaurantservice.restaurant.model.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class MenuCategoryMapper {

    public MenuCategory toEntity(CreateMenuCategoryRequest request, Restaurant restaurant) {
        int displayOrder = request.displayOrder() == null ? 0 : request.displayOrder();

        return MenuCategory.builder()
                .restaurant(restaurant)
                .name(request.name())
                .displayOrder(displayOrder)
                .build();
    }

    public void updateEntity(
            UpdateMenuCategoryRequest request,
            MenuCategory menuCategory
    ) {
        menuCategory.updateDetails(
                request.name(),
                request.displayOrder(),
                request.active()
        );
    }

    public MenuCategoryResponse toResponse(MenuCategory menuCategory) {
        return new MenuCategoryResponse(
                menuCategory.getId(),
                menuCategory.getRestaurant().getId(),
                menuCategory.getName(),
                menuCategory.getDisplayOrder(),
                menuCategory.isActive(),
                menuCategory.getCreatedAt(),
                menuCategory.getUpdatedAt()
        );
    }
}
