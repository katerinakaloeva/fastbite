package com.fastbite.restaurantservice.restaurant.exception;

import java.util.UUID;

public class RestaurantAccessDeniedException extends ApplicationException {

    public RestaurantAccessDeniedException(UUID restaurantId) {
        super(
                ErrorCode.RESTAURANT_ACCESS_DENIED,
                "You are not allowed to manage restaurant " + restaurantId
        );
    }
}
