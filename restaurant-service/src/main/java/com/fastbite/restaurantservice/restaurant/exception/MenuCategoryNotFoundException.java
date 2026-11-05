package com.fastbite.restaurantservice.restaurant.exception;

import java.util.UUID;

public class MenuCategoryNotFoundException extends ApplicationException {

    public MenuCategoryNotFoundException(UUID categoryId) {
        super(
                ErrorCode.MENU_CATEGORY_NOT_FOUND,
                "Menu category with id " + categoryId + " was not found"
        );
    }
}
