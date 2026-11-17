package com.fastbite.restaurantservice.restaurant.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateRestaurantRequest(

        @Size(min = 1, max = 100)
        @Pattern(regexp = ".*\\S.*", message = "Restaurant name must not be blank")
        String name,

        @Size(min = 1, max = 255)
        @Pattern(regexp = ".*\\S.*", message = "Restaurant address must not be blank")
        String address,

        String description,

        @Size(max = 30)
        String phone,

        @Size(max = 100)
        String city,

        @Size(min = 1, max = 100)
        @Pattern(regexp = ".*\\S.*", message = "Municipality must not be blank")
        String municipality,

        @Size(min = 1, max = 100)
        @Pattern(regexp = ".*\\S.*", message = "Region must not be blank")
        String region,

        @Size(max = 20)
        String postalCode,

        Boolean active
) {
}
