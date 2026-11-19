package com.fastbite.identityservice.identity.dto;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        Set<String> roles,
        OffsetDateTime createdAt
) {
}
