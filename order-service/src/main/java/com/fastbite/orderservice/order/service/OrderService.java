package com.fastbite.orderservice.order.service;

import com.fastbite.orderservice.order.client.RestaurantClient;
import com.fastbite.orderservice.order.dto.RestaurantMenuResponse;
import com.fastbite.orderservice.order.dto.CreateOrderItemRequest;
import com.fastbite.orderservice.order.dto.CreateOrderRequest;
import com.fastbite.orderservice.order.dto.OrderResponse;
import com.fastbite.orderservice.order.exception.InvalidOrderException;
import com.fastbite.orderservice.order.mapper.OrderMapper;
import com.fastbite.orderservice.order.model.Order;
import com.fastbite.orderservice.order.repository.OrderRepository;
import com.fastbite.orderservice.order.exception.MenuItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final RestaurantClient restaurantClient;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(UUID restaurantId, CreateOrderRequest request) {
        validateItems(request.items());

        List<UUID> menuItemIds = request.items()
                .stream()
                .map(CreateOrderItemRequest::menuItemId)
                .distinct()
                .toList();

        Map<UUID, RestaurantMenuResponse.Item> itemsById = restaurantClient
                .getOrderableItems(restaurantId)
                .stream()
                .filter(item -> menuItemIds.contains(item.id()))
                .collect(Collectors.toMap(RestaurantMenuResponse.Item::id, Function.identity()));

        for (UUID menuItemId : menuItemIds) {
            if (!itemsById.containsKey(menuItemId)) {
                throw new MenuItemNotFoundException(menuItemId);
            }
        }

        String currency = itemsById.get(menuItemIds.getFirst()).currency();
        if (itemsById.values().stream().anyMatch(item -> !currency.equals(item.currency()))) {
            throw new InvalidOrderException("All order items must use the same currency");
        }

        Order order = orderMapper.toEntity(request, restaurantId, currency);

        for (CreateOrderItemRequest itemRequest : request.items()) {
            RestaurantMenuResponse.Item item = itemsById.get(itemRequest.menuItemId());
            order.addItem(item.id(), item.name(), item.price(), itemRequest.quantity());
        }

        // The orders.total_amount column is NUMERIC(10, 2).
        if (order.getTotalAmount().compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new InvalidOrderException("Order total exceeds the supported amount");
        }

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    private void validateItems(List<CreateOrderItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new InvalidOrderException("Order must contain at least one item");
        }

        for (CreateOrderItemRequest item : items) {
            if (item == null || item.menuItemId() == null
                    || item.quantity() == null || item.quantity() < 1) {
                throw new InvalidOrderException("Each order item must have an ID and a positive quantity");
            }
        }
    }
}
