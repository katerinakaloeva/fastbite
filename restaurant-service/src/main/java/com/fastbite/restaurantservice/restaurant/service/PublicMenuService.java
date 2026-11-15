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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicMenuService {

    private final RestaurantRepository restaurantRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final PublicMenuMapper publicMenuMapper;

    @Transactional(readOnly = true)
    public PublicMenuResponse getPublicMenu(UUID restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .filter(Restaurant::isActive)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        List<MenuCategory> categories = menuCategoryRepository
                .findAllByRestaurant_IdAndActiveTrueOrderByDisplayOrderAsc(
                        restaurantId
                );

        if (categories.isEmpty()) {
            return publicMenuMapper.toResponse(restaurant, categories, Map.of());
        }

        List<UUID> categoryIds = categories
                .stream()
                .map(MenuCategory::getId)
                .toList();

        List<MenuItem> items = menuItemRepository
                .findAllByCategory_IdInAndAvailableTrue(categoryIds);

        Map<UUID, List<MenuItem>> itemsByCategory = items
                .stream()
                .collect(Collectors.groupingBy(
                        item -> item.getCategory().getId()
                ));

        return publicMenuMapper.toResponse(restaurant, categories, itemsByCategory);
    }
}