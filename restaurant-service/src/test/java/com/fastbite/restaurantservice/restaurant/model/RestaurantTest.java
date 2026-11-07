package com.fastbite.restaurantservice.restaurant.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RestaurantTest {

    @Test
    void updatesOnlyTheFieldsProvided() {
        Restaurant restaurant = Restaurant.builder()
                .name("FastBite Athens")
                .address("Athens, Greece")
                .description("Original description")
                .phone("+30 210 000 0000")
                .city("Athens")
                .postalCode("10552")
                .active(true)
                .build();

        restaurant.updateDetails(
                null,
                null,
                null,
                "+30 210 987 6543",
                null,
                null,
                false
        );

        assertEquals("FastBite Athens", restaurant.getName());
        assertEquals("Athens, Greece", restaurant.getAddress());
        assertEquals("Original description", restaurant.getDescription());
        assertEquals("+30 210 987 6543", restaurant.getPhone());
        assertEquals("Athens", restaurant.getCity());
        assertEquals("10552", restaurant.getPostalCode());
        assertFalse(restaurant.isActive());
    }
}
