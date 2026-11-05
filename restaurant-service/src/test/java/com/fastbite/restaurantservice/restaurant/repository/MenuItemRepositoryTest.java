package com.fastbite.restaurantservice.restaurant.repository;

import com.fastbite.restaurantservice.restaurant.model.MenuCategory;
import com.fastbite.restaurantservice.restaurant.model.MenuItem;
import com.fastbite.restaurantservice.restaurant.model.Restaurant;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MenuItemRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findsOnlyAvailableItemsByCategory() {
        Restaurant restaurant = restaurantRepository.saveAndFlush(
                Restaurant.builder()
                        .name("FastBite Athens")
                        .address("Athens, Greece")
                        .build()
        );

        MenuCategory category = menuCategoryRepository.saveAndFlush(
                MenuCategory.builder()
                        .restaurant(restaurant)
                        .name("Burgers")
                        .build()
        );

        MenuItem cheeseburger = MenuItem.builder()
                .category(category)
                .name("Cheeseburger")
                .price(new BigDecimal("8.50"))
                .currency("EUR")
                .build();

        MenuItem veganBurger = MenuItem.builder()
                .category(category)
                .name("Vegan Burger")
                .price(new BigDecimal("8.00"))
                .currency("EUR")
                .available(false)
                .build();

        MenuItem chickenBurger = MenuItem.builder()
                .category(category)
                .name("Chicken Burger")
                .price(new BigDecimal("7.50"))
                .currency("EUR")
                .build();

        menuItemRepository.saveAllAndFlush(List.of(cheeseburger, veganBurger, chickenBurger));

        entityManager.clear();

        List<MenuItem> menuItems = menuItemRepository
                .findAllByCategory_IdAndAvailableTrue(category.getId());

        assertThat(menuItems)
                .extracting(MenuItem::getName)
                .containsExactlyInAnyOrder("Cheeseburger", "Chicken Burger");
    }
}
