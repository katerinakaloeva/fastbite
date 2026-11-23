package com.fastbite.restaurantservice.restaurant.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

/**
 * Test users and fake JWTs for controller and service tests.
 */
public final class TestUsers {

    public static final UUID OWNER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID OTHER_OWNER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final UUID ADMIN_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    public static final CurrentUser OWNER = new CurrentUser(OWNER_ID, false);
    public static final CurrentUser OTHER_OWNER = new CurrentUser(OTHER_OWNER_ID, false);
    public static final CurrentUser ADMIN = new CurrentUser(ADMIN_ID, true);

    private TestUsers() {
    }

    public static JwtRequestPostProcessor owner() {
        return user(OWNER_ID, "RESTAURANT_OWNER");
    }

    public static JwtRequestPostProcessor customer() {
        return user(UUID.randomUUID(), "CUSTOMER");
    }

    public static JwtRequestPostProcessor admin() {
        return user(ADMIN_ID, "ADMIN");
    }

    private static JwtRequestPostProcessor user(UUID id, String role) {
        return jwt()
                .jwt(token -> token.subject(id.toString()).claim("roles", List.of(role)))
                .authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }
}
