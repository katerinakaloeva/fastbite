package com.fastbite.orderservice.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateOrderRequest(

        @NotBlank(message = "Customer name is required")
        @Size(max = 100, message = "Customer name must not exceed 100 characters")
        String customerName,

        @NotBlank(message = "Customer phone is required")
        @Size(max = 30, message = "Customer phone must not exceed 30 characters")
        String customerPhone,

        @NotBlank(message = "Delivery address is required")
        @Size(max = 255, message = "Delivery address must not exceed 255 characters")
        String deliveryAddress,

        @NotEmpty(message = "Order must contain at least one item")
        List<@NotNull(message = "Order item is required") @Valid CreateOrderItemRequest> items
) {
}
