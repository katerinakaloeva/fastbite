package com.fastbite.identityservice.identity.service;

import com.fastbite.identityservice.config.JwtConfig;
import com.fastbite.identityservice.config.JwtProperties;
import com.fastbite.identityservice.identity.model.Role;
import com.fastbite.identityservice.identity.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class JwtServiceTest {

    private static final String SECRET = "test-secret-that-is-at-least-32-characters-long";

    private JwtService jwtService;
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties(SECRET, Duration.ofMinutes(15), "fastbite-identity-service");
        JwtConfig config = new JwtConfig();
        SecretKey key = config.jwtSigningKey(properties);

        jwtService = new JwtService(config.jwtEncoder(key), properties);
        jwtDecoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Test
    void generatesSignedTokenWithUserIdEmailRolesAndExpiry() {
        UUID userId = UUID.randomUUID();
        User user = userWithId(userId);

        Jwt jwt = jwtDecoder.decode(jwtService.generateToken(user));

        assertThat(jwt.getSubject()).isEqualTo(userId.toString());
        assertThat(jwt.getClaimAsString("email")).isEqualTo("maria@example.com");
        assertThat(jwt.getClaimAsStringList("roles")).containsExactly("CUSTOMER");
        assertThat(jwt.getClaimAsString("iss")).isEqualTo("fastbite-identity-service");
        assertThat(jwt.getExpiresAt()).isCloseTo(jwt.getIssuedAt().plus(Duration.ofMinutes(15)), within(1, ChronoUnit.SECONDS));
        assertThat(jwtService.getExpirationSeconds()).isEqualTo(900);
    }

    @Test
    void tokenSignedWithAnotherSecretIsRejected() {
        String token = jwtService.generateToken(userWithId(UUID.randomUUID()));

        SecretKey otherKey = new SecretKeySpec(
                "another-secret-that-is-also-32-characters".getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        JwtDecoder otherDecoder = NimbusJwtDecoder.withSecretKey(otherKey).macAlgorithm(MacAlgorithm.HS256).build();

        assertThatThrownBy(() -> otherDecoder.decode(token)).isInstanceOf(JwtException.class);
    }

    private User userWithId(UUID id) {
        User user = User.builder()
                .fullName("Maria Papadopoulou")
                .email("maria@example.com")
                .passwordHash("hash")
                .roles(Set.of(Role.builder().name("CUSTOMER").build()))
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}
