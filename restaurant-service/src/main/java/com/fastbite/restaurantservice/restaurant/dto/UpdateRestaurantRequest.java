package com.fastbite.restaurantservice.restaurant.dto;

import jakarta.validation.constraints.Size;

public record UpdateRestaurantRequest(

        @Size(min = 1, max = 100)
        String name,

        @Size(min = 1, max = 255)
        String address,

        String description,

        @Size(max = 30)
        String phone,

        @Size(max = 100)
        String city,

        @Size(max = 20)
        String postalCode,

        Boolean active
) {
}