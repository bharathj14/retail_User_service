package com.example.retail.controllers;

import com.example.retail.entities.User;
import com.example.retail.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Additional imports
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "john.doe@example.com", roles = {"USER"})
    void authenticatedUser_ReturnsCurrentUser() throws Exception {
        // Arrange
        User user = new User();
        user.setEmail("john.doe@example.com");
        user.setName("John Doe");
        user.setRoles(Set.of("ROLE_USER"));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act & Assert
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void allUsers_ReturnsListOfUsers() throws Exception {
        // Arrange
        User user1 = new User();
        user1.setEmail("john.doe@example.com");
        user1.setName("John Doe");

        User user2 = new User();
        user2.setEmail("jane.doe@example.com");
        user2.setName("Jane Doe");

        when(userService.allUsers()).thenReturn(List.of(user1, user2));

        // Act & Assert
        mockMvc.perform(get("/users/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[1].email").value("jane.doe@example.com"));
    }
}
