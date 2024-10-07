package com.example.retail.services;

import com.example.retail.entities.User;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Set up the secret key and expiration time
        String secret = "your-256-bit-secret-key-which-should-be-long-enough";
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        String encodedKey = Encoders.BASE64.encode(key.getEncoded());
        jwtService.secretKey = encodedKey;
        jwtService.jwtExpiration = 3600000L; // 1 hour
    }

    @Test
    void generateToken_ValidUser_ReturnsToken() {
        // Arrange
        UserDetails userDetails = createUser();

        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        assertNotNull(token);
    }

    @Test
    void extractUsername_ValidToken_ReturnsUsername() {
        // Arrange
        UserDetails userDetails = createUser();
        String token = jwtService.generateToken(userDetails);

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void isTokenValid_ValidToken_ReturnsTrue() {
        // Arrange
        UserDetails userDetails = createUser();
        String token = jwtService.generateToken(userDetails);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_InvalidToken_ReturnsFalse() {
        // Arrange
        UserDetails userDetails = createUser();
        String token = "invalid.token";

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertFalse(isValid);
    }

    private UserDetails createUser() {
        User user = new User();
        user.setEmail("john.doe@example.com");
        user.setPassword("encodedPassword");
        user.setRoles(Set.of("ROLE_USER"));
        return user;
    }
}
