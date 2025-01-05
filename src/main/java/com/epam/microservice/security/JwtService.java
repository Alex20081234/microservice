package com.epam.microservice.security;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class JwtService {
    private final String key;

    @Autowired
    public JwtService(@Value("${microservice.secretKey}") String key) {
        this.key = key;
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey()).build().parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    private SecretKey secretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return new SecretKeySpec(decodedKey, "HmacSHA256");
    }
}
