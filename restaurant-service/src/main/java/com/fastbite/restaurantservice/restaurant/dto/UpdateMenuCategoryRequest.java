package com.fastbite.restaurantservice.restaurant.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdateMenuCategoryRequest(

        @Size(min = 1, max = 100)
        @Pattern(regexp = ".*\\S.*", message = "Menu category name must not be blank")
        String name,

        @PositiveOrZero
        Integer displayOrder,

        Boolean active
) {
}
