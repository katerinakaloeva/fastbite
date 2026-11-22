package com.fastbite.identityservice.identity.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
