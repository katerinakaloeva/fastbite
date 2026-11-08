package com.fastbite.restaurantservice.restaurant.service;

import com.fastbite.restaurantservice.restaurant.dto.CreateMenuCategoryRequest;
import com.fastbite.restaurantservice.restaurant.dto.MenuCategoryResponse;
import com.fastbite.restaurantservice.restaurant.dto.UpdateMenuCategoryRequest;
import com.fastbite.restaurantservice.restaurant.exception.MenuCategoryNotFoundException;
import com.fastbite.restaurantservice.restaurant.exception.RestaurantNotFoundException;
import com.fastbite.restaurantservice.restaurant.mapper.MenuCategoryMapper;
import com.fastbite.restaurantservice.restaurant.model.MenuCategory;
import com.fastbite.restaurantservice.restaurant.model.Restaurant;
import com.fastbite.restaurantservice.restaurant.repository.MenuCategoryRepository;
import com.fastbite.restaurantservice.restaurant.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class MenuCategoryServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuCategoryRepository menuCategoryRepository;

    @Mock
    private MenuCategoryMapper menuCategoryMapper;

    @InjectMocks
    private MenuCategoryService menuCategoryService;

    @Test
    void createsMenuCategoryWhenRestaurantExists() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = restaurant(restaurantId);
        CreateMenuCategoryRequest request = new CreateMenuCategoryRequest("Burgers", null);
        MenuCategory categoryToSave = menuCategory(restaurant, "Burgers", 0);
        MenuCategory savedCategory = MenuCategory.builder()
                .id(UUID.randomUUID())
                .restaurant(restaurant)
                .name("Burgers")
                .displayOrder(0)
                .build();
        MenuCategoryResponse expectedResponse = categoryResponse(savedCategory);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuCategoryMapper.toEntity(request, restaurant)).thenReturn(categoryToSave);
        when(menuCategoryRepository.save(categoryToSave)).thenReturn(savedCategory);
        when(menuCategoryMapper.toResponse(savedCategory)).thenReturn(expectedResponse);

        MenuCategoryResponse result = menuCategoryService.createMenuCategory(restaurantId, request);

        assertSame(expectedResponse, result);
        verify(restaurantRepository).findById(restaurantId);
        verify(menuCategoryMapper).toEntity(request, restaurant);
        verify(menuCategoryRepository).save(categoryToSave);
        verify(menuCategoryMapper).toResponse(savedCategory);
    }

    @Test
    void returnsCategoriesForRestaurantInDisplayOrder() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = restaurant(restaurantId);
        MenuCategory burgers = menuCategory(restaurant, "Burgers", 0);
        MenuCategory salads = menuCategory(restaurant, "Salads", 1);
        MenuCategoryResponse burgersResponse = categoryResponse(burgers);
        MenuCategoryResponse saladsResponse = categoryResponse(salads);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuCategoryRepository.findAllByRestaurant_IdOrderByDisplayOrderAsc(restaurantId))
                .thenReturn(List.of(burgers, salads));
        when(menuCategoryMapper.toResponse(burgers)).thenReturn(burgersResponse);
        when(menuCategoryMapper.toResponse(salads)).thenReturn(saladsResponse);

        List<MenuCategoryResponse> result = menuCategoryService.getCategoriesByRestaurantId(restaurantId);

        assertEquals(List.of(burgersResponse, saladsResponse), result);
        verify(restaurantRepository).findById(restaurantId);
        verify(menuCategoryRepository).findAllByRestaurant_IdOrderByDisplayOrderAsc(restaurantId);
        verify(menuCategoryMapper).toResponse(burgers);
        verify(menuCategoryMapper).toResponse(salads);
    }

    @Test
    void updatesMenuCategoryWhenItBelongsToRestaurant() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = restaurant(restaurantId);
        MenuCategory menuCategory = menuCategory(restaurant, "Burgers", 0);
        UpdateMenuCategoryRequest request = new UpdateMenuCategoryRequest(
                "Main dishes",
                1,
                false
        );
        MenuCategoryResponse expectedResponse = new MenuCategoryResponse(
                menuCategory.getId(),
                restaurantId,
                "Main dishes",
                1,
                false,
                null,
                null
        );

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuCategoryRepository.findById(menuCategory.getId()))
                .thenReturn(Optional.of(menuCategory));
        when(menuCategoryMapper.toResponse(menuCategory)).thenReturn(expectedResponse);

        MenuCategoryResponse result = menuCategoryService.updateMenuCategory(
                restaurantId,
                menuCategory.getId(),
                request
        );

        assertSame(expectedResponse, result);
        verify(restaurantRepository).findById(restaurantId);
        verify(menuCategoryRepository).findById(menuCategory.getId());
        verify(menuCategoryMapper).updateEntity(request, menuCategory);
        verify(menuCategoryMapper).toResponse(menuCategory);
    }

    @Test
    void throwsExceptionWhenRestaurantDoesNotExist() {
        UUID restaurantId = UUID.randomUUID();

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        assertThrows(
                RestaurantNotFoundException.class,
                () -> menuCategoryService.getCategoriesByRestaurantId(restaurantId)
        );

        verify(restaurantRepository).findById(restaurantId);
        verifyNoInteractions(menuCategoryRepository, menuCategoryMapper);
    }

    @Test
    void throwsExceptionWhenUpdatingMenuCategoryForMissingRestaurant() {
        UUID restaurantId = UUID.randomUUID();
        UpdateMenuCategoryRequest request = new UpdateMenuCategoryRequest("Burgers", null, null);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        assertThrows(
                RestaurantNotFoundException.class,
                () -> menuCategoryService.updateMenuCategory(
                        restaurantId,
                        UUID.randomUUID(),
                        request
                )
        );

        verify(restaurantRepository).findById(restaurantId);
        verifyNoInteractions(menuCategoryRepository, menuCategoryMapper);
    }

    @Test
    void throwsExceptionWhenUpdatingMenuCategoryThatDoesNotExist() {
        UUID restaurantId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        Restaurant restaurant = restaurant(restaurantId);
        UpdateMenuCategoryRequest request = new UpdateMenuCategoryRequest("Burgers", null, null);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(
                MenuCategoryNotFoundException.class,
                () -> menuCategoryService.updateMenuCategory(restaurantId, categoryId, request)
        );

        verify(restaurantRepository).findById(restaurantId);
        verify(menuCategoryRepository).findById(categoryId);
        verifyNoInteractions(menuCategoryMapper);
    }

    @Test
    void throwsExceptionWhenMenuCategoryBelongsToAnotherRestaurant() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = restaurant(restaurantId);
        Restaurant anotherRestaurant = restaurant(UUID.randomUUID());
        MenuCategory menuCategory = menuCategory(anotherRestaurant, "Burgers", 0);
        UpdateMenuCategoryRequest request = new UpdateMenuCategoryRequest("Burgers", null, null);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuCategoryRepository.findById(menuCategory.getId()))
                .thenReturn(Optional.of(menuCategory));

        assertThrows(
                MenuCategoryNotFoundException.class,
                () -> menuCategoryService.updateMenuCategory(
                        restaurantId,
                        menuCategory.getId(),
                        request
                )
        );

        verify(restaurantRepository).findById(restaurantId);
        verify(menuCategoryRepository).findById(menuCategory.getId());
        verifyNoInteractions(menuCategoryMapper);
    }

    private Restaurant restaurant(UUID restaurantId) {
        return Restaurant.builder()
                .id(restaurantId)
                .name("FastBite Athens")
                .address("Athens, Greece")
                .build();
    }

    private MenuCategory menuCategory(Restaurant restaurant, String name, int displayOrder) {
        return MenuCategory.builder()
                .id(UUID.randomUUID())
                .restaurant(restaurant)
                .name(name)
                .displayOrder(displayOrder)
                .build();
    }

    private MenuCategoryResponse categoryResponse(MenuCategory menuCategory) {
        return new MenuCategoryResponse(
                menuCategory.getId(),
                menuCategory.getRestaurant().getId(),
                menuCategory.getName(),
                menuCategory.getDisplayOrder(),
                menuCategory.isActive(),
                null,
                null
        );
    }
}
