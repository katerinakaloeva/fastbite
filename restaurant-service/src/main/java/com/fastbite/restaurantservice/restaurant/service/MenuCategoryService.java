package com.fastbite.restaurantservice.restaurant.service;

import com.fastbite.restaurantservice.restaurant.dto.CreateMenuCategoryRequest;
import com.fastbite.restaurantservice.restaurant.dto.MenuCategoryResponse;
import com.fastbite.restaurantservice.restaurant.dto.UpdateMenuCategoryRequest;
import com.fastbite.restaurantservice.restaurant.exception.MenuCategoryNotFoundException;
import com.fastbite.restaurantservice.restaurant.exception.RestaurantAccessDeniedException;
import com.fastbite.restaurantservice.restaurant.exception.RestaurantNotFoundException;
import com.fastbite.restaurantservice.restaurant.mapper.MenuCategoryMapper;
import com.fastbite.restaurantservice.restaurant.model.MenuCategory;
import com.fastbite.restaurantservice.restaurant.model.Restaurant;
import com.fastbite.restaurantservice.restaurant.repository.MenuCategoryRepository;
import com.fastbite.restaurantservice.restaurant.repository.RestaurantRepository;
import com.fastbite.restaurantservice.restaurant.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuCategoryService {

    private final RestaurantRepository restaurantRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuCategoryMapper menuCategoryMapper;

    @Transactional
    public MenuCategoryResponse createMenuCategory(
            UUID restaurantId,
            CreateMenuCategoryRequest request,
            CurrentUser currentUser
    ) {
        Restaurant restaurant = findRestaurantById(restaurantId);
        checkCanManage(restaurant, currentUser);

        MenuCategory menuCategory = menuCategoryMapper.toEntity(request, restaurant);
        MenuCategory savedMenuCategory = menuCategoryRepository.save(menuCategory);

        return menuCategoryMapper.toResponse(savedMenuCategory);
    }

    @Transactional(readOnly = true)
    public List<MenuCategoryResponse> getCategoriesByRestaurantId(UUID restaurantId) {
        findRestaurantById(restaurantId);

        return menuCategoryRepository.findAllByRestaurant_IdOrderByDisplayOrderAsc(restaurantId)
                .stream()
                .map(menuCategoryMapper::toResponse)
                .toList();
    }

    @Transactional
    public MenuCategoryResponse updateMenuCategory(
            UUID restaurantId,
            UUID categoryId,
            UpdateMenuCategoryRequest request,
            CurrentUser currentUser
    ) {
        Restaurant restaurant = findRestaurantById(restaurantId);
        checkCanManage(restaurant, currentUser);
        MenuCategory menuCategory = findMenuCategoryByIdAndRestaurantId(
                categoryId,
                restaurant.getId()
        );

        menuCategoryMapper.updateEntity(request, menuCategory);

        return menuCategoryMapper.toResponse(menuCategory);
    }

    private void checkCanManage(Restaurant restaurant, CurrentUser currentUser) {
        if (!currentUser.canManage(restaurant)) {
            throw new RestaurantAccessDeniedException(restaurant.getId());
        }
    }

    private Restaurant findRestaurantById(UUID restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
    }

    private MenuCategory findMenuCategoryByIdAndRestaurantId(
            UUID categoryId,
            UUID restaurantId
    ) {
        return menuCategoryRepository.findById(categoryId)
                .filter(menuCategory -> menuCategory.getRestaurant().getId().equals(restaurantId))
                .orElseThrow(() -> new MenuCategoryNotFoundException(categoryId));
    }
}
