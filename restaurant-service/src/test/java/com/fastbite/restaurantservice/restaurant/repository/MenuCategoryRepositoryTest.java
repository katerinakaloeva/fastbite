package com.fastbite.restaurantservice.restaurant.repository;

import com.fastbite.restaurantservice.restaurant.model.MenuCategory;
import com.fastbite.restaurantservice.restaurant.model.Restaurant;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MenuCategoryRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findsCategoriesByRestaurantInDisplayOrder() {
        Restaurant restaurant = restaurantRepository.saveAndFlush(
                Restaurant.builder()
                        .name("FastBite Athens")
                        .address("Athens, Greece")
                        .build()
        );

        MenuCategory drinks = MenuCategory.builder()
                .restaurant(restaurant)
                .name("Drinks")
                .displayOrder(2)
                .build();

        MenuCategory burgers = MenuCategory.builder()
                .restaurant(restaurant)
                .name("Burgers")
                .displayOrder(0)
                .build();

        MenuCategory salads = MenuCategory.builder()
                .restaurant(restaurant)
                .name("Salads")
                .displayOrder(1)
                .build();

        menuCategoryRepository.saveAllAndFlush(List.of(drinks, burgers, salads));

        entityManager.clear();

        List<MenuCategory> categories = menuCategoryRepository
                .findAllByRestaurant_IdOrderByDisplayOrderAsc(restaurant.getId());

        assertThat(categories)
                .extracting(MenuCategory::getName)
                .containsExactly("Burgers", "Salads", "Drinks");
    }
}
