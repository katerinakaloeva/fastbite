package com.fastbite.orderservice.order.client;

import com.fastbite.orderservice.order.dto.RestaurantMenuResponse;

import com.fastbite.orderservice.order.exception.RestaurantNotFoundException;
import com.fastbite.orderservice.order.exception.RestaurantServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RestaurantClient {

    private final RestClient restaurantRestClient;

    public List<RestaurantMenuResponse.Item> getOrderableItems(UUID restaurantId) {
        try {
            RestaurantMenuResponse menu = restaurantRestClient.get()
                    .uri("/restaurants/{restaurantId}/menu", restaurantId)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (request, response) -> {
                        throw new RestaurantNotFoundException(restaurantId);
                    })
                    .body(RestaurantMenuResponse.class);

            if (menu == null || !restaurantId.equals(menu.restaurantId()) || menu.categories() == null
                    || menu.categories().stream().anyMatch(category -> category == null || category.items() == null)) {
                throw new RestaurantServiceUnavailableException();
            }
            List<RestaurantMenuResponse.Item> items = menu.categories().stream()
                    .flatMap(category -> category.items().stream()).toList();
            if (items.stream().anyMatch(item -> item == null || item.id() == null || item.name() == null
                    || item.price() == null || item.price().signum() < 0
                    || item.currency() == null || !item.currency().matches("[A-Z]{3}"))
                    || items.stream().map(RestaurantMenuResponse.Item::id).distinct().count() != items.size()) {
                throw new RestaurantServiceUnavailableException();
            }
            return items;
        } catch (RestClientException exception) {
            throw new RestaurantServiceUnavailableException();
        }
    }
}