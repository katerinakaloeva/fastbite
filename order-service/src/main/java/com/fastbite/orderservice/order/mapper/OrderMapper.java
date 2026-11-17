package com.fastbite.orderservice.order.mapper;

import com.fastbite.orderservice.order.dto.CreateOrderRequest;
import com.fastbite.orderservice.order.dto.OrderItemResponse;
import com.fastbite.orderservice.order.dto.OrderResponse;
import com.fastbite.orderservice.order.model.Order;
import com.fastbite.orderservice.order.model.OrderItem;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public Order toEntity(
            CreateOrderRequest request,
            UUID restaurantId,
            String currency
    ) {
        return Order.builder()
                .restaurantId(restaurantId)
                .customerName(request.customerName())
                .customerPhone(request.customerPhone())
                .deliveryAddress(request.deliveryAddress())
                .currency(currency)
                .build();
    }

    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getRestaurantId(),
                order.getStatus(),
                order.getItems().stream()
                        .map(this::toItemResponse)
                        .toList(),
                order.getTotalAmount(),
                order.getCurrency()
        );
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getMenuItemId(),
                item.getItemName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getLineTotal()
        );
    }
}
