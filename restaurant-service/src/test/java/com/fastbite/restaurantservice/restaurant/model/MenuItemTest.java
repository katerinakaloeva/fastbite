package com.fastbite.restaurantservice.restaurant.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class MenuItemTest {

    @Test
    void updatesOnlyTheFieldsProvided() {
        Restaurant restaurant = Restaurant.builder()
                .name("FastBite Athens")
                .address("Athens, Greece")
                .build();
        MenuCategory category = MenuCategory.builder()
                .restaurant(restaurant)
                .name("Burgers")
                .build();
        MenuItem menuItem = MenuItem.builder()
                .category(category)
                .name("Cheeseburger")
                .description("Beef burger with cheese")
                .price(new BigDecimal("8.50"))
                .currency("EUR")
                .available(true)
                .build();

        menuItem.updateDetails(null, null, new BigDecimal("9.50"), null, false);

        assertEquals("Cheeseburger", menuItem.getName());
        assertEquals("Beef burger with cheese", menuItem.getDescription());
        assertEquals(new BigDecimal("9.50"), menuItem.getPrice());
        assertEquals("EUR", menuItem.getCurrency());
        assertFalse(menuItem.isAvailable());
    }
}
