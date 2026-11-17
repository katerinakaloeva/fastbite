package com.fastbite.orderservice.order.controller;

import com.fastbite.orderservice.config.RestaurantClientConfig;
import com.fastbite.orderservice.order.exception.InvalidOrderException;
import com.fastbite.orderservice.order.exception.RestaurantServiceUnavailableException;
import com.fastbite.orderservice.order.exception.RestaurantNotFoundException;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import com.fastbite.orderservice.order.dto.CreateOrderItemRequest;
import com.fastbite.orderservice.order.dto.CreateOrderRequest;
import com.fastbite.orderservice.order.dto.OrderItemResponse;
import com.fastbite.orderservice.order.dto.OrderResponse;
import com.fastbite.orderservice.order.model.OrderStatus;
import com.fastbite.orderservice.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(RestaurantClientConfig.class)
@TestPropertySource(properties = {
        "restaurant-service.base-url=http://localhost:8080",
        "restaurant-service.connect-timeout=2s",
        "restaurant-service.read-timeout=5s"
})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void createsOrderAndReturnsCreatedResponseWithoutCustomerContactDetails() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();
        OrderResponse response = new OrderResponse(
                orderId,
                restaurantId,
                OrderStatus.PENDING,
                List.of(new OrderItemResponse(
                        menuItemId,
                        "Burger",
                        2,
                        new BigDecimal("8.50"),
                        new BigDecimal("17.00")
                )),
                new BigDecimal("17.00"),
                "EUR"
        );

        when(orderService.createOrder(eq(restaurantId), any(CreateOrderRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/restaurants/{restaurantId}/orders", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerName": "Maria",
                                  "customerPhone": "2101234567",
                                  "deliveryAddress": "Athens, Greece",
                                  "items": [
                                    {
                                      "menuItemId": "%s",
                                      "quantity": 2
                                    }
                                  ]
                                }
                                """.formatted(menuItemId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.restaurantId").value(restaurantId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.items[0].menuItemId").value(menuItemId.toString()))
                .andExpect(jsonPath("$.items[0].itemName").value("Burger"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].unitPrice").value(8.50))
                .andExpect(jsonPath("$.items[0].lineTotal").value(17.00))
                .andExpect(jsonPath("$.totalAmount").value(17.00))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.customerPhone").doesNotExist())
                .andExpect(jsonPath("$.deliveryAddress").doesNotExist());

        verify(orderService).createOrder(
                eq(restaurantId),
                eq(new CreateOrderRequest(
                        "Maria",
                        "2101234567",
                        "Athens, Greece",
                        List.of(new CreateOrderItemRequest(
                                menuItemId, 2
                        ))
                ))
        );
    }

    @Test
    void returnsValidationErrorAndDoesNotCallServiceWhenRequestIsInvalid() throws Exception {
        UUID restaurantId = UUID.randomUUID();

        mockMvc.perform(post("/restaurants/{restaurantId}/orders", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerName": "",
                                  "customerPhone": "2101234567",
                                  "deliveryAddress": "Athens, Greece",
                                  "items": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.customerName").value("Customer name is required"))
                .andExpect(jsonPath("$.fieldErrors.items").value("Order must contain at least one item"));

        verifyNoInteractions(orderService);
    }

    @Test
    void rejectsNullOrderItemBeforeCallingService() throws Exception {
        mockMvc.perform(post("/restaurants/{restaurantId}/orders", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody("[null]")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors['items[0]']").value("Order item is required"));
        verifyNoInteractions(orderService);
    }

    @Test
    void validatesNestedItemFieldsBeforeCallingService() throws Exception {
        mockMvc.perform(post("/restaurants/{restaurantId}/orders", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody("[{ \"menuItemId\": null, \"quantity\": 0 }]")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors['items[0].menuItemId']").value("Menu item ID is required"))
                .andExpect(jsonPath("$.fieldErrors['items[0].quantity']").value("Quantity must be at least 1"));
        verifyNoInteractions(orderService);
    }

    @Test
    void returnsNotFoundCodeAndMessageForMissingRestaurant() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        when(orderService.createOrder(eq(restaurantId), any(CreateOrderRequest.class)))
                .thenThrow(new RestaurantNotFoundException(restaurantId));

        mockMvc.perform(post("/restaurants/{restaurantId}/orders", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON).content(validRequestBody()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESTAURANT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Restaurant with id " + restaurantId + " was not found"));
    }

    @Test
    void returnsServiceUnavailableCodeAndMessageForRemoteFailure() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        when(orderService.createOrder(eq(restaurantId), any(CreateOrderRequest.class)))
                .thenThrow(new RestaurantServiceUnavailableException());

        mockMvc.perform(post("/restaurants/{restaurantId}/orders", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON).content(validRequestBody()))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("RESTAURANT_SERVICE_UNAVAILABLE"))
                .andExpect(jsonPath("$.message").value("Restaurant service is temporarily unavailable"));
    }

    @Test
    void returnsOrderInvalidCodeAndMessageForBusinessRuleFailure() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        when(orderService.createOrder(eq(restaurantId), any(CreateOrderRequest.class)))
                .thenThrow(new InvalidOrderException(
                        "All order items must use the same currency"));

        mockMvc.perform(post("/restaurants/{restaurantId}/orders", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON).content(validRequestBody()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ORDER_INVALID"))
                .andExpect(jsonPath("$.message").value("All order items must use the same currency"));
    }

    private String validRequestBody() {
        return requestBody("[{ \"menuItemId\": \"" + UUID.randomUUID() + "\", \"quantity\": 1 }]");
    }

    private String requestBody(String items) {
        return """
                {
                  "customerName": "Maria",
                  "customerPhone": "2101234567",
                  "deliveryAddress": "Athens, Greece",
                  "items": %s
                }
                """.formatted(items);
    }}
