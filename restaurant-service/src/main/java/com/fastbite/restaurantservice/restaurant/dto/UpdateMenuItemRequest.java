package com.fastbite.restaurantservice.restaurant.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateMenuItemRequest(

        @Size(min = 1, max = 150)
        @Pattern(regexp = ".*\\S.*", message = "Menu item name must not be blank")
        String name,

        String description,

        @DecimalMin("0.00")
        @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 integer digits and 2 decimal places")
        BigDecimal price,

        @Pattern(regexp = "^[A-Z]{3}$")
        String currency,

        Boolean available
) {
}
