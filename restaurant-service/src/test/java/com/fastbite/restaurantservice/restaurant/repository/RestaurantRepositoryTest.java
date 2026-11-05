package com.fastbite.restaurantservice.restaurant.repository;

import com.fastbite.restaurantservice.restaurant.model.Restaurant;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RestaurantRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void savesAndFindsRestaurantById() {
        Restaurant restaurant = Restaurant.builder()
                .name("FastBite Athens")
                .address("Athens, Greece")
                .description("Greek street food")
                .phone("2101234567")
                .city("Athens")
                .postalCode("10558")
                .build();

        Restaurant savedRestaurant = restaurantRepository.saveAndFlush(restaurant);

        entityManager.clear();

        Restaurant foundRestaurant = restaurantRepository
                .findById(savedRestaurant.getId())
                .orElseThrow();

        assertThat(foundRestaurant.getName()).isEqualTo("FastBite Athens");
        assertThat(foundRestaurant.getAddress()).isEqualTo("Athens, Greece");
        assertThat(foundRestaurant.isActive()).isTrue();
        assertThat(foundRestaurant.getDescription()).isEqualTo("Greek street food");
        assertThat(foundRestaurant.getPhone()).isEqualTo("2101234567");
        assertThat(foundRestaurant.getCity()).isEqualTo("Athens");
        assertThat(foundRestaurant.getPostalCode()).isEqualTo("10558");
        assertThat(foundRestaurant.getCreatedAt()).isNotNull();
        assertThat(foundRestaurant.getUpdatedAt()).isNotNull();
    }
}
