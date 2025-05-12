package com.arun.user_service.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.arun.user_service.models.Token;
import com.arun.user_service.models.User;
import com.arun.user_service.repositories.TokenRepository;
import com.arun.user_service.repositories.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @BeforeEach
    void setUp() {
        // Create a user with a known token
        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password"); // You may hash this if needed
        User newUser = userRepository.save(user);

        Token token = new Token();
        token.setToken("abc123");
        token.setUser(newUser);
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        tokenRepository.save(token);
    }

    @AfterEach
    void tearDown() {
        User deleteUser = userRepository.findByUsername("john").get();
        userRepository.delete(deleteUser);
        // List<Token> tokens = tokenRepository.findByUser(deleteUser);

        // for(Token t : tokens){
        //     tokenRepository.deleteById(t.getId());
        // }
    }

    @Test
    void test_signup_whenNewUser_returnsSuccess() throws Exception {
        String newUser = """
                {
                    "username": "newuser",
                    "password": "password123",
                    "email": "newuser@example.com",
                    "firstName": "Test",
                    "lastName": "User"
                }
                """;

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newUser))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void test_login_whenValidCredentials_returnsToken() throws Exception {
        String loginRequest = """
                {
                    "username": "john",
                    "password": "password"
                }
                """;

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("abc123"));
    }

    @Test
    void test_logout_withValidToken_returnsSuccess() throws Exception {
        mockMvc.perform(delete("/auth/logout")
                .header("Authorization", "Bearer abc123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));
    }

    @Test
    void test_verifyToken_withValidToken_returnsProfile() throws Exception {
        mockMvc.perform(get("/auth/verify")
                .header("Authorization", "Bearer abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }
}
