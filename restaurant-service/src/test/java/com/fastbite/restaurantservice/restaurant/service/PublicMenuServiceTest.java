package com.fastbite.restaurantservice.restaurant.service;

import com.fastbite.restaurantservice.restaurant.dto.PublicMenuResponse;
import com.fastbite.restaurantservice.restaurant.exception.RestaurantNotFoundException;
import com.fastbite.restaurantservice.restaurant.mapper.PublicMenuMapper;
import com.fastbite.restaurantservice.restaurant.model.MenuCategory;
import com.fastbite.restaurantservice.restaurant.model.MenuItem;
import com.fastbite.restaurantservice.restaurant.model.Restaurant;
import com.fastbite.restaurantservice.restaurant.repository.MenuCategoryRepository;
import com.fastbite.restaurantservice.restaurant.repository.MenuItemRepository;
import com.fastbite.restaurantservice.restaurant.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicMenuServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuCategoryRepository menuCategoryRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    private PublicMenuService publicMenuService;

    @BeforeEach
    void setUp() {
        publicMenuService = new PublicMenuService(
                restaurantRepository,
                menuCategoryRepository,
                menuItemRepository,
                new PublicMenuMapper()
        );
    }

    @Test
    void returnsActiveCategoriesAndAvailableItemsGroupedByCategory() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = restaurant(restaurantId, true);
        MenuCategory burgers = category(UUID.randomUUID(), restaurant, "Burgers", 1);
        MenuCategory drinks = category(UUID.randomUUID(), restaurant, "Drinks", 2);
        MenuItem burger = item(UUID.randomUUID(), burgers, "Classic Burger");
        MenuItem drink = item(UUID.randomUUID(), drinks, "Cola");
        List<UUID> categoryIds = List.of(burgers.getId(), drinks.getId());

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuCategoryRepository
                .findAllByRestaurant_IdAndActiveTrueOrderByDisplayOrderAsc(restaurantId))
                .thenReturn(List.of(burgers, drinks));
        when(menuItemRepository.findAllByCategory_IdInAndAvailableTrue(categoryIds))
                .thenReturn(List.of(drink, burger));

        PublicMenuResponse result = publicMenuService.getPublicMenu(restaurantId);

        assertEquals(restaurantId, result.restaurantId());
        assertEquals("FastBite Athens", result.restaurantName());
        assertEquals(List.of("Burgers", "Drinks"), result.categories().stream()
                .map(category -> category.name())
                .toList());
        assertEquals(List.of("Classic Burger"), result.categories().get(0).items().stream()
                .map(menuItem -> menuItem.name())
                .toList());
        assertEquals(List.of("Cola"), result.categories().get(1).items().stream()
                .map(menuItem -> menuItem.name())
                .toList());

        verify(menuCategoryRepository)
                .findAllByRestaurant_IdAndActiveTrueOrderByDisplayOrderAsc(restaurantId);
        verify(menuItemRepository).findAllByCategory_IdInAndAvailableTrue(categoryIds);
    }

    @Test
    void returnsRestaurantWithEmptyCategoriesWhenItHasNoActiveCategories() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = restaurant(restaurantId, true);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuCategoryRepository
                .findAllByRestaurant_IdAndActiveTrueOrderByDisplayOrderAsc(restaurantId))
                .thenReturn(List.of());

        PublicMenuResponse result = publicMenuService.getPublicMenu(restaurantId);

        assertEquals(restaurantId, result.restaurantId());
        assertEquals("FastBite Athens", result.restaurantName());
        assertEquals(List.of(), result.categories());
        verifyNoInteractions(menuItemRepository);
    }

    @Test
    void throwsNotFoundWhenRestaurantDoesNotExist() {
        UUID restaurantId = UUID.randomUUID();
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        assertThrows(
                RestaurantNotFoundException.class,
                () -> publicMenuService.getPublicMenu(restaurantId)
        );

        verifyNoInteractions(menuCategoryRepository, menuItemRepository);
    }

    @Test
    void throwsNotFoundWhenRestaurantIsInactive() {
        UUID restaurantId = UUID.randomUUID();
        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.of(restaurant(restaurantId, false)));

        assertThrows(
                RestaurantNotFoundException.class,
                () -> publicMenuService.getPublicMenu(restaurantId)
        );

        verifyNoInteractions(menuCategoryRepository, menuItemRepository);
    }

    private Restaurant restaurant(UUID id, boolean active) {
        return Restaurant.builder()
                .id(id)
                .name("FastBite Athens")
                .address("Athens, Greece")
                .active(active)
                .build();
    }

    private MenuCategory category(
            UUID id,
            Restaurant restaurant,
            String name,
            int displayOrder
    ) {
        return MenuCategory.builder()
                .id(id)
                .restaurant(restaurant)
                .name(name)
                .displayOrder(displayOrder)
                .build();
    }

    private MenuItem item(UUID id, MenuCategory category, String name) {
        return MenuItem.builder()
                .id(id)
                .category(category)
                .name(name)
                .description("Test description")
                .price(new BigDecimal("8.50"))
                .currency("EUR")
                .build();
    }
}
