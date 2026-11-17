package com.fastbite.restaurantservice.restaurant.mapper;

import com.fastbite.restaurantservice.restaurant.dto.PublicRestaurantDetailsResponse;
import com.fastbite.restaurantservice.restaurant.dto.PublicRestaurantSummaryResponse;
import com.fastbite.restaurantservice.restaurant.model.Restaurant;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PublicRestaurantMapperTest {

    private final PublicRestaurantMapper publicRestaurantMapper = new PublicRestaurantMapper();

    @Test
    void mapsRestaurantToPublicSummaryResponse() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = restaurant(restaurantId);

        PublicRestaurantSummaryResponse response = publicRestaurantMapper.toResponse(restaurant);

        assertEquals(
                new PublicRestaurantSummaryResponse(
                        restaurantId,
                        "FastBite Athens",
                        "Fast food restaurant",
                        "Athens",
                        "Athens Municipality",
                        "Attica"
                ),
                response
        );
    }

    @Test
    void mapsRestaurantToPublicDetailsResponse() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = restaurant(restaurantId);

        PublicRestaurantDetailsResponse response = publicRestaurantMapper
                .toDetailsResponse(restaurant);

        assertEquals(
                new PublicRestaurantDetailsResponse(
                        restaurantId,
                        "FastBite Athens",
                        "Fast food restaurant",
                        "Athens, Greece",
                        "+30 210 123 4567",
                        "Athens",
                        "Athens Municipality",
                        "Attica",
                        "10552"
                ),
                response
        );
    }

    private Restaurant restaurant(UUID restaurantId) {
        return Restaurant.builder()
                .id(restaurantId)
                .name("FastBite Athens")
                .address("Athens, Greece")
                .description("Fast food restaurant")
                .phone("+30 210 123 4567")
                .city("Athens")
                .municipality("Athens Municipality")
                .region("Attica")
                .postalCode("10552")
                .build();
    }
}
