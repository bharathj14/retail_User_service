package com.example.retail.controllers;

import com.example.retail.dtos.LoginUserDto;
import com.example.retail.dtos.RegisterUserDto;
import com.example.retail.entities.User;
import com.example.retail.response.LoginResponse;
import com.example.retail.services.AuthenticationService;
import com.example.retail.services.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    void register_ValidUser_ReturnsOk() throws Exception {
        // Arrange
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setName("John Doe");
        registerUserDto.setEmail("john.doe@example.com");
        registerUserDto.setPassword("password123");

        User user = new User();
        user.setId(1);
        user.setName(registerUserDto.getName());
        user.setEmail(registerUserDto.getEmail());

        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(user);

        // Act & Assert
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(registerUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void authenticate_ValidCredentials_ReturnsToken() throws Exception {
        // Arrange
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("john.doe@example.com");
        loginUserDto.setPassword("password123");

        User user = new User();
        user.setEmail(loginUserDto.getEmail());
        user.setPassword("encodedPassword");

        when(authenticationService.authenticate(any(LoginUserDto.class))).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("test.jwt.token");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test.jwt.token"));
    }

    // Utility method to convert objects to JSON strings
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
