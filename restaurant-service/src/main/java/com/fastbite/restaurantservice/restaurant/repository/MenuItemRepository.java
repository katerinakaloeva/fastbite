package com.fastbite.restaurantservice.restaurant.repository;

import com.fastbite.restaurantservice.restaurant.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {

    List<MenuItem> findAllByCategory_IdAndAvailableTrue(UUID categoryId);
}