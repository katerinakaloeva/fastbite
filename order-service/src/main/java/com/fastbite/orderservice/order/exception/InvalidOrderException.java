package com.fastbite.orderservice.order.exception;


public class InvalidOrderException extends ApplicationException {

    public InvalidOrderException(String message) {
        super(ErrorCode.ORDER_INVALID, message);
    }
}
