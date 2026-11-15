package com.fastbite.restaurantservice.restaurant.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateMenuItemRequest(

        @Size(min = 1, max = 150)
        @Pattern(regexp = ".*\\S.*", message = "Menu item name must not be blank")
        String name,

        String description,

        @DecimalMin("0.00")
        BigDecimal price,

        @Pattern(regexp = "^[A-Z]{3}$")
        String currency,

        Boolean available
) {
}
