package com.fastbite.orderservice.order.service;

import com.fastbite.orderservice.order.client.RestaurantClient;
import com.fastbite.orderservice.order.dto.RestaurantMenuResponse.Item;
import com.fastbite.orderservice.order.exception.MenuItemNotFoundException;
import com.fastbite.orderservice.order.exception.RestaurantNotFoundException;
import com.fastbite.orderservice.order.exception.RestaurantServiceUnavailableException;
import com.fastbite.orderservice.order.dto.CreateOrderItemRequest;
import com.fastbite.orderservice.order.dto.CreateOrderRequest;
import com.fastbite.orderservice.order.dto.OrderResponse;
import com.fastbite.orderservice.order.exception.InvalidOrderException;
import com.fastbite.orderservice.order.mapper.OrderMapper;
import com.fastbite.orderservice.order.model.Order;
import com.fastbite.orderservice.order.model.OrderStatus;
import com.fastbite.orderservice.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private RestaurantClient restaurantClient;
    @Mock
    private OrderRepository orderRepository;

    private OrderService orderService;
    private final UUID restaurantId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        orderService = new OrderService(restaurantClient, orderRepository, new OrderMapper());
    }

    @Test
    void createsOrderUsingRemotePricesAndStoresIndependentSnapshots() {
        Item burger = item("Burger", "8.50", "EUR");
        Item drink = item("Cola", "2.30", "EUR");
        UUID orderId = UUID.randomUUID();
        when(restaurantClient.getOrderableItems(restaurantId)).thenReturn(List.of(drink, burger));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            return Order.builder().id(orderId).restaurantId(order.getRestaurantId())
                    .items(order.getItems()).totalAmount(order.getTotalAmount())
                    .currency(order.getCurrency()).build();
        });

        OrderResponse result = orderService.createOrder(restaurantId, request(
                new CreateOrderItemRequest(burger.id(), 2),
                new CreateOrderItemRequest(drink.id(), 3)
        ));

        assertEquals(orderId, result.id());
        assertEquals(restaurantId, result.restaurantId());
        assertEquals(OrderStatus.PENDING, result.status());
        assertEquals("EUR", result.currency());
        assertEquals(new BigDecimal("23.90"), result.totalAmount());
        assertEquals(burger.id(), result.items().getFirst().menuItemId());
        assertEquals("Burger", result.items().getFirst().itemName());
        assertEquals(2, result.items().getFirst().quantity());
        assertEquals(new BigDecimal("8.50"), result.items().getFirst().unitPrice());
        assertEquals(new BigDecimal("17.00"), result.items().getFirst().lineTotal());

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        Order saved = captor.getValue();
        assertEquals("Guest", saved.getCustomerName());
        assertEquals("2100000000", saved.getCustomerPhone());
        assertEquals("Test address", saved.getDeliveryAddress());
        assertTrue(saved.getItems().stream().allMatch(item -> item.getOrder() == saved));
        // A later menu response cannot alter the stored order snapshot.
        when(restaurantClient.getOrderableItems(restaurantId)).thenReturn(List.of(
                new Item(burger.id(), "New burger", null, new BigDecimal("99.00"), "EUR")
        ));
        assertEquals(new BigDecimal("99.00"), restaurantClient.getOrderableItems(restaurantId).getFirst().price());
        assertEquals("Burger", new OrderMapper().toResponse(saved).items().getFirst().itemName());
        assertEquals(new BigDecimal("23.90"), saved.getTotalAmount());
    }

    @Test
    void rejectsMissingOrInactiveRestaurantWithoutSaving() {
        when(restaurantClient.getOrderableItems(restaurantId))
                .thenThrow(new RestaurantNotFoundException(restaurantId));

        assertThrows(RestaurantNotFoundException.class, () -> orderService.createOrder(
                restaurantId, request(new CreateOrderItemRequest(UUID.randomUUID(), 1))
        ));
        verifyNoInteractions(orderRepository);
    }

    @Test
    void doesNotSaveWhenRestaurantServiceIsUnavailable() {
        when(restaurantClient.getOrderableItems(restaurantId))
                .thenThrow(new RestaurantServiceUnavailableException());

        assertThrows(RestaurantServiceUnavailableException.class, () -> orderService.createOrder(
                restaurantId, request(new CreateOrderItemRequest(UUID.randomUUID(), 1))
        ));
        verifyNoInteractions(orderRepository);
    }

    @Test
    void rejectsRequestedItemMissingFromPublicMenu() {
        Item burger = item("Burger", "8.50", "EUR");
        when(restaurantClient.getOrderableItems(restaurantId)).thenReturn(List.of(burger));

        assertThrows(MenuItemNotFoundException.class, () -> orderService.createOrder(restaurantId, request(
                new CreateOrderItemRequest(burger.id(), 1),
                new CreateOrderItemRequest(UUID.randomUUID(), 1)
        )));
        verifyNoInteractions(orderRepository);
    }

    @Test
    void rejectsMixedCurrenciesAmongRequestedItems() {
        Item burger = item("Burger", "8.50", "EUR");
        Item drink = item("Cola", "2.30", "USD");
        when(restaurantClient.getOrderableItems(restaurantId)).thenReturn(List.of(burger, drink));

        assertThrows(InvalidOrderException.class, () -> orderService.createOrder(restaurantId, request(
                new CreateOrderItemRequest(burger.id(), 1), new CreateOrderItemRequest(drink.id(), 1)
        )));
        verifyNoInteractions(orderRepository);
    }

    @Test
    void ignoresUnrequestedMenuCurrenciesAndCountsRepeatedLines() {
        Item burger = item("Burger", "8.50", "EUR");
        when(restaurantClient.getOrderableItems(restaurantId))
                .thenReturn(List.of(burger, item("Other", "2.00", "USD")));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse result = orderService.createOrder(restaurantId, request(
                new CreateOrderItemRequest(burger.id(), 2), new CreateOrderItemRequest(burger.id(), 3)
        ));
        assertEquals(2, result.items().size());
        assertEquals(new BigDecimal("42.50"), result.totalAmount());
    }

    @Test
    void rejectsInvalidItemsBeforeRemoteCall() {
        for (CreateOrderRequest invalid : List.of(
                request(), new CreateOrderRequest("Guest", "2100000000", "Test address", null),
                request((CreateOrderItemRequest) null), request(new CreateOrderItemRequest(null, 1)),
                request(new CreateOrderItemRequest(UUID.randomUUID(), null)),
                request(new CreateOrderItemRequest(UUID.randomUUID(), 0)),
                request(new CreateOrderItemRequest(UUID.randomUUID(), -1))
        )) {
            assertThrows(InvalidOrderException.class, () -> orderService.createOrder(restaurantId, invalid));
        }
        verifyNoInteractions(restaurantClient, orderRepository);
    }

    @Test
    void rejectsTotalExceedingDatabaseCapacity() {
        Item expensive = item("Expensive", "99999999.99", "EUR");
        when(restaurantClient.getOrderableItems(restaurantId)).thenReturn(List.of(expensive));

        assertThrows(InvalidOrderException.class, () -> orderService.createOrder(restaurantId, request(
                new CreateOrderItemRequest(expensive.id(), 2)
        )));
        verifyNoInteractions(orderRepository);
    }

    private Item item(String name, String price, String currency) {
        return new Item(UUID.randomUUID(), name, null, new BigDecimal(price), currency);
    }

    private CreateOrderRequest request(CreateOrderItemRequest... items) {
        return new CreateOrderRequest("Guest", "2100000000", "Test address", Arrays.asList(items));
    }
}