package com.fastbite.restaurantservice.restaurant.service;

import com.fastbite.restaurantservice.restaurant.dto.CreateMenuItemRequest;
import com.fastbite.restaurantservice.restaurant.dto.MenuItemResponse;
import com.fastbite.restaurantservice.restaurant.exception.MenuCategoryNotFoundException;
import com.fastbite.restaurantservice.restaurant.mapper.MenuItemMapper;
import com.fastbite.restaurantservice.restaurant.model.MenuCategory;
import com.fastbite.restaurantservice.restaurant.model.MenuItem;
import com.fastbite.restaurantservice.restaurant.model.Restaurant;
import com.fastbite.restaurantservice.restaurant.repository.MenuCategoryRepository;
import com.fastbite.restaurantservice.restaurant.repository.MenuItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceTest {

    @Mock
    private MenuCategoryRepository menuCategoryRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private MenuItemMapper menuItemMapper;

    @InjectMocks
    private MenuItemService menuItemService;

    @Test
    void createsMenuItemWhenCategoryExists() {
        UUID categoryId = UUID.randomUUID();
        MenuCategory category = menuCategory(categoryId);
        CreateMenuItemRequest request = new CreateMenuItemRequest(
                "Cheeseburger",
                "Beef burger with cheese",
                new BigDecimal("8.50"),
                "EUR"
        );
        MenuItem menuItemToSave = menuItem(category, "Cheeseburger", new BigDecimal("8.50"));
        MenuItem savedMenuItem = MenuItem.builder()
                .id(UUID.randomUUID())
                .category(category)
                .name("Cheeseburger")
                .description("Beef burger with cheese")
                .price(new BigDecimal("8.50"))
                .currency("EUR")
                .build();
        MenuItemResponse expectedResponse = menuItemResponse(savedMenuItem);

        when(menuCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(menuItemMapper.toEntity(request, category)).thenReturn(menuItemToSave);
        when(menuItemRepository.save(menuItemToSave)).thenReturn(savedMenuItem);
        when(menuItemMapper.toResponse(savedMenuItem)).thenReturn(expectedResponse);

        MenuItemResponse result = menuItemService.createMenuItem(categoryId, request);

        assertSame(expectedResponse, result);
        verify(menuCategoryRepository).findById(categoryId);
        verify(menuItemMapper).toEntity(request, category);
        verify(menuItemRepository).save(menuItemToSave);
        verify(menuItemMapper).toResponse(savedMenuItem);
    }

    @Test
    void returnsAvailableItemsForCategory() {
        UUID categoryId = UUID.randomUUID();
        MenuCategory category = menuCategory(categoryId);
        MenuItem cheeseburger = menuItem(category, "Cheeseburger", new BigDecimal("8.50"));
        MenuItem chickenBurger = menuItem(category, "Chicken Burger", new BigDecimal("7.50"));
        MenuItemResponse cheeseburgerResponse = menuItemResponse(cheeseburger);
        MenuItemResponse chickenBurgerResponse = menuItemResponse(chickenBurger);

        when(menuCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(menuItemRepository.findAllByCategory_IdAndAvailableTrue(categoryId))
                .thenReturn(List.of(cheeseburger, chickenBurger));
        when(menuItemMapper.toResponse(cheeseburger)).thenReturn(cheeseburgerResponse);
        when(menuItemMapper.toResponse(chickenBurger)).thenReturn(chickenBurgerResponse);

        List<MenuItemResponse> result = menuItemService.getAvailableItemsByCategoryId(categoryId);

        assertEquals(List.of(cheeseburgerResponse, chickenBurgerResponse), result);
        verify(menuCategoryRepository).findById(categoryId);
        verify(menuItemRepository).findAllByCategory_IdAndAvailableTrue(categoryId);
        verify(menuItemMapper).toResponse(cheeseburger);
        verify(menuItemMapper).toResponse(chickenBurger);
    }

    @Test
    void throwsExceptionWhenCategoryDoesNotExist() {
        UUID categoryId = UUID.randomUUID();

        when(menuCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(
                MenuCategoryNotFoundException.class,
                () -> menuItemService.getAvailableItemsByCategoryId(categoryId)
        );

        verify(menuCategoryRepository).findById(categoryId);
        verifyNoInteractions(menuItemRepository, menuItemMapper);
    }

    private MenuCategory menuCategory(UUID categoryId) {
        Restaurant restaurant = Restaurant.builder()
                .id(UUID.randomUUID())
                .name("FastBite Athens")
                .address("Athens, Greece")
                .build();

        return MenuCategory.builder()
                .id(categoryId)
                .restaurant(restaurant)
                .name("Burgers")
                .displayOrder(0)
                .build();
    }

    private MenuItem menuItem(MenuCategory category, String name, BigDecimal price) {
        return MenuItem.builder()
                .id(UUID.randomUUID())
                .category(category)
                .name(name)
                .price(price)
                .currency("EUR")
                .build();
    }

    private MenuItemResponse menuItemResponse(MenuItem menuItem) {
        return new MenuItemResponse(
                menuItem.getId(),
                menuItem.getCategory().getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getCurrency(),
                menuItem.isAvailable(),
                null,
                null
        );
    }
}
