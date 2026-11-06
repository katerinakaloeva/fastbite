package com.fastbite.restaurantservice.restaurant.service;

import com.fastbite.restaurantservice.restaurant.dto.CreateMenuItemRequest;
import com.fastbite.restaurantservice.restaurant.dto.MenuItemResponse;
import com.fastbite.restaurantservice.restaurant.exception.MenuCategoryNotFoundException;
import com.fastbite.restaurantservice.restaurant.mapper.MenuItemMapper;
import com.fastbite.restaurantservice.restaurant.model.MenuCategory;
import com.fastbite.restaurantservice.restaurant.model.MenuItem;
import com.fastbite.restaurantservice.restaurant.repository.MenuCategoryRepository;
import com.fastbite.restaurantservice.restaurant.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuItemMapper menuItemMapper;

    @Transactional
    public MenuItemResponse createMenuItem(
            UUID categoryId,
            CreateMenuItemRequest request
    ) {
        MenuCategory category = findMenuCategoryById(categoryId);

        MenuItem menuItem = menuItemMapper.toEntity(request, category);
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);

        return menuItemMapper.toResponse(savedMenuItem);
    }

    @Transactional(readOnly = true)
    public List<MenuItemResponse> getAvailableItemsByCategoryId(UUID categoryId) {
        findMenuCategoryById(categoryId);

        return menuItemRepository.findAllByCategory_IdAndAvailableTrue(categoryId)
                .stream()
                .map(menuItemMapper::toResponse)
                .toList();
    }

    private MenuCategory findMenuCategoryById(UUID categoryId) {
        return menuCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new MenuCategoryNotFoundException(categoryId));
    }
}
