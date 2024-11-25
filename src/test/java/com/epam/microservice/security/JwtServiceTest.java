package com.epam.microservice.security;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

class JwtServiceTest {
    private final String key = "A2L8YVx0gfXUJpA5p3lBzX9K8klcmXUOvPjH4FbbJCI=";
    private final JwtService jwtService = new JwtService(key);

    @Test
    void validateJwtTokenShouldReturnTrueWhenTokenValid() {
        assertTrue(jwtService.validateJwtToken(generateJwtToken()));
    }

    @Test
    void validateJwtTokenShouldReturnFalseWhenTokenInvalid() {
        String invalidToken = "invalidToken";
        assertFalse(jwtService.validateJwtToken(invalidToken));
    }

    @Test
    void parseJwtShouldReturnTokenFromAuthorizationHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String token = generateJwtToken();
        request.addHeader("Authorization", "Bearer " + token);
        String parsedToken = jwtService.parseJwt(request);
        assertEquals(token, parsedToken);
    }

    @Test
    void parseJwtShouldReturnNullWhenInvalidAuthorizationHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "InvalidHeader " + generateJwtToken());
        assertNull(jwtService.parseJwt(request));
        request.addHeader("Authorization", "");
        assertNull(jwtService.parseJwt(request));
        MockHttpServletRequest requestWithoutHeader = new MockHttpServletRequest();
        assertNull(jwtService.parseJwt(requestWithoutHeader));
    }

    private String generateJwtToken() {
        return Jwts.builder()
                .subject(("Test token"))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(secretKey())
                .compact();
    }

    private SecretKey secretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return new SecretKeySpec(decodedKey, "HmacSHA256");
    }
}
