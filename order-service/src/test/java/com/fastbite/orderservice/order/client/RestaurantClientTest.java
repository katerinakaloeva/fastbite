package com.fastbite.orderservice.order.client;

import com.fastbite.orderservice.order.dto.RestaurantMenuResponse;

import com.fastbite.orderservice.order.exception.RestaurantNotFoundException;
import com.fastbite.orderservice.order.exception.RestaurantServiceUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class RestaurantClientTest {

    private final UUID restaurantId = UUID.randomUUID();
    private MockRestServiceServer server;
    private RestaurantClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://restaurant");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new RestaurantClient(builder.build());
    }

    @Test
    void readsExistingRestaurantMenuContract() {
        UUID itemId = UUID.randomUUID();
        server.expect(requestTo(url())).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "restaurantId": "%s",
                          "restaurantName": "FastBite",
                          "categories": [{
                            "id": "%s", "name": "Burgers", "displayOrder": 1,
                            "items": [{
                              "id": "%s", "name": "Burger", "description": "Classic",
                              "price": 8.50, "currency": "EUR"
                            }]
                          }]
                        }
                        """.formatted(restaurantId, UUID.randomUUID(), itemId), MediaType.APPLICATION_JSON));

        var items = client.getOrderableItems(restaurantId);
        assertEquals(1, items.size());
        assertEquals(itemId, items.getFirst().id());
        assertEquals(new BigDecimal("8.50"), items.getFirst().price());
        server.verify();
    }

    @Test
    void mapsMissingOrInactiveRestaurantToNotFound() {
        server.expect(requestTo(url())).andRespond(withStatus(HttpStatus.NOT_FOUND));
        assertThrows(RestaurantNotFoundException.class, () -> client.getOrderableItems(restaurantId));
        server.verify();
    }

    @Test
    void mapsRemoteFailureToServiceUnavailable() {
        server.expect(requestTo(url())).andRespond(withServerError());
        assertThrows(RestaurantServiceUnavailableException.class, () -> client.getOrderableItems(restaurantId));
        server.verify();
    }

    @Test
    void mapsNetworkFailureToServiceUnavailable() {
        server.expect(requestTo(url())).andRespond(withException(new IOException("Connection unavailable")));
        assertThrows(RestaurantServiceUnavailableException.class, () -> client.getOrderableItems(restaurantId));
        server.verify();
    }

    @Test
    void rejectsUnexpectedRestaurantInResponse() {
        server.expect(requestTo(url())).andRespond(withSuccess("""
                {"restaurantId": "%s", "restaurantName": "Other", "categories": []}
                """.formatted(UUID.randomUUID()), MediaType.APPLICATION_JSON));
        assertThrows(RestaurantServiceUnavailableException.class, () -> client.getOrderableItems(restaurantId));
        server.verify();
    }
    private String url() {
        return "http://restaurant/restaurants/" + restaurantId + "/menu";
    }

    @Test
    void rejectsDuplicateIdsAndMalformedMenuData() {
        UUID itemId = UUID.randomUUID();
        String item = """
                {"id": "%s", "name": "Burger", "price": 8.50, "currency": "EUR"}
                """.formatted(itemId);
        String duplicateItems = """
                {"restaurantId": "%s", "categories": [
                  {"id": "%s", "name": "Burgers", "displayOrder": 1, "items": [%s, %s]}
                ]}
                """.formatted(restaurantId, UUID.randomUUID(), item, item);
        for (String body : java.util.List.of(
                duplicateItems,
                "{\"restaurantId\": \"" + restaurantId + "\", \"categories\": null}",
                "{\"restaurantId\": \"" + restaurantId + "\", \"categories\": [null]}",
                "{ invalid json }"
        )) {
            server.expect(requestTo(url())).andRespond(withSuccess(body, MediaType.APPLICATION_JSON));
            assertThrows(RestaurantServiceUnavailableException.class, () -> client.getOrderableItems(restaurantId));
            server.verify();
            server.reset();
        }
    }}