package com.fastbite.restaurantservice.restaurant.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    RESTAURANT_NOT_FOUND(HttpStatus.NOT_FOUND),
    MENU_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND),
    MENU_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus status;

    ErrorCode(HttpStatus status) {
        this.status = status;
    }
}