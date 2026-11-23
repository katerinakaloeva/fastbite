package com.fastbite.restaurantservice.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtDecoderTest {

    private static final String SECRET = "test-secret-that-is-at-least-32-characters-long";
    private static final String ISSUER = "fastbite-identity-service";

    private final JwtDecoder decoder = new SecurityConfig().jwtDecoder(SECRET, ISSUER);

    @Test
    void acceptsTokenSignedByIdentityServiceKey() {
        String token = token(SECRET, ISSUER, Instant.now().plus(Duration.ofMinutes(15)));

        Jwt jwt = decoder.decode(token);

        assertThat(jwt.getSubject()).isEqualTo("11111111-1111-1111-1111-111111111111");
        assertThat(jwt.getClaimAsStringList("roles")).containsExactly("RESTAURANT_OWNER");
    }

    @Test
    void rejectsTokenSignedWithAnotherKey() {
        String token = token("another-secret-that-is-also-32-characters", ISSUER, Instant.now().plus(Duration.ofMinutes(15)));

        assertThatThrownBy(() -> decoder.decode(token)).isInstanceOf(JwtException.class);
    }

    @Test
    void rejectsTokenFromAnotherIssuer() {
        String token = token(SECRET, "some-other-system", Instant.now().plus(Duration.ofMinutes(15)));

        assertThatThrownBy(() -> decoder.decode(token)).isInstanceOf(JwtException.class);
    }

    @Test
    void rejectsExpiredToken() {
        String token = token(SECRET, ISSUER, Instant.now().minus(Duration.ofHours(1)));

        assertThatThrownBy(() -> decoder.decode(token)).isInstanceOf(JwtException.class);
    }

    private String token(String secret, String issuer, Instant expiresAt) {
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        NimbusJwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject("11111111-1111-1111-1111-111111111111")
                .issuedAt(expiresAt.minus(Duration.ofMinutes(15)))
                .expiresAt(expiresAt)
                .claim("roles", List.of("RESTAURANT_OWNER"))
                .build();

        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
    }
}
