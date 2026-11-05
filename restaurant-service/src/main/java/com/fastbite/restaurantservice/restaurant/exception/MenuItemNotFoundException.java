package com.fastbite.restaurantservice.restaurant.exception;

import java.util.UUID;

public class MenuItemNotFoundException extends ApplicationException {

    public MenuItemNotFoundException(UUID menuItemId) {
        super(
                ErrorCode.MENU_ITEM_NOT_FOUND,
                "Menu item with id " + menuItemId + " was not found"
        );
    }
}
