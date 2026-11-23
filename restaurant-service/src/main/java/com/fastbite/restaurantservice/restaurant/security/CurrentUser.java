package com.fastbite.restaurantservice.restaurant.security;

import com.fastbite.restaurantservice.restaurant.model.Restaurant;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.UUID;

public record CurrentUser(UUID id, boolean admin) {

    public static CurrentUser from(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");
        boolean admin = roles != null && roles.contains("ADMIN");

        return new CurrentUser(UUID.fromString(jwt.getSubject()), admin);
    }

    public boolean canManage(Restaurant restaurant) {
        return admin || restaurant.isOwnedBy(id);
    }
}
