package com.fastbite.restaurantservice.restaurant.exception;

import java.util.UUID;

public class RestaurantNotFoundException extends ApplicationException {

    public RestaurantNotFoundException(UUID restaurantId) {
        super(
                ErrorCode.RESTAURANT_NOT_FOUND,
                "Restaurant with id " + restaurantId + " was not found"
        );
    }
}
