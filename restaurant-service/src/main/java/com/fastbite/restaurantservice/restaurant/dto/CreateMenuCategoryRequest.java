package com.fastbite.restaurantservice.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateMenuCategoryRequest(

        @NotBlank(message = "Menu category name is required")
        @Size(max = 100, message = "Menu category name must not exceed 100 characters")
        String name,

        @PositiveOrZero(message = "Display order must be zero or greater")
        Integer displayOrder
) {
}