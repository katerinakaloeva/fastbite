package com.fastbite.restaurantservice.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRestaurantRequest(

        @NotBlank(message = "Restaurant name is required")
        @Size(max = 100, message = "Restaurant name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Restaurant address is required")
        @Size(max = 255, message = "Restaurant address must not exceed 255 characters")
        String address,

        String description,

        @Size(max = 30, message = "Phone must not exceed 30 characters")
        String phone,

        @Size(max = 100, message = "City must not exceed 100 characters")
        String city,

        @Size(max = 20, message = "Postal code must not exceed 20 characters")
        String postalCode
) {
}