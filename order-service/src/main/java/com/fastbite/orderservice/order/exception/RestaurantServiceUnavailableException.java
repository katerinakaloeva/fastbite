package com.fastbite.orderservice.order.exception;

public class RestaurantServiceUnavailableException extends ApplicationException {

    public RestaurantServiceUnavailableException() {
        super(ErrorCode.RESTAURANT_SERVICE_UNAVAILABLE, "Restaurant service is temporarily unavailable");
    }
}