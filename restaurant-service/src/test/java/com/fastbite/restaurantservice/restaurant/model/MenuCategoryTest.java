package com.fastbite.restaurantservice.restaurant.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class MenuCategoryTest {

    @Test
    void updatesOnlyTheFieldsProvided() {
        Restaurant restaurant = Restaurant.builder()
                .name("FastBite Athens")
                .address("Athens, Greece")
                .build();
        MenuCategory menuCategory = MenuCategory.builder()
                .restaurant(restaurant)
                .name("Pizza")
                .displayOrder(0)
                .active(true)
                .build();

        menuCategory.updateDetails(null, 2, false);

        assertEquals("Pizza", menuCategory.getName());
        assertEquals(2, menuCategory.getDisplayOrder());
        assertFalse(menuCategory.isActive());
    }
}
