package com.fastbite.orderservice.order.controller;

import com.fastbite.orderservice.order.dto.CreateOrderRequest;
import com.fastbite.orderservice.order.dto.OrderResponse;
import com.fastbite.orderservice.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(
        name = "Orders",
        description = "Operations for placing restaurant orders."
)
@RestController
@RequestMapping("/restaurants/{restaurantId}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "Create an order",
            description = "Creates a guest order for an active restaurant."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid order data or items."),
            @ApiResponse(responseCode = "404", description = "Restaurant or menu item not found."),
            @ApiResponse(responseCode = "503", description = "Restaurant service is unavailable.")
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        OrderResponse response = orderService.createOrder(restaurantId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
