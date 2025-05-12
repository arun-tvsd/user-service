package com.arun.user_service.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.arun.user_service.models.Token;
import com.arun.user_service.repositories.TokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.arun.user_service.models.User;
import com.arun.user_service.repositories.UserRepository;
import com.arun.user_service.services.IUserService;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;


    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password"); // You may hash this if needed
        user = userRepository.save(user); // Save the user and get the persisted user entity

        // Create and persist the token associated with the user
        Token token = new Token();
        token.setToken("abc123");
        token.setUser(user);  // Set the user reference correctly
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        tokenRepository.save(token);
    }

    @AfterEach
    void tearDown() {
        Optional<User> userOpt = userRepository.findByUsername("john");
        userOpt.ifPresent(deleteUser -> {
            tokenRepository.deleteByUser(deleteUser);
            userRepository.delete(deleteUser);
        });

        Optional<User> user2Opt = userRepository.findByUsername("john2");
        user2Opt.ifPresent(deleteUser -> {
            tokenRepository.deleteByUser(deleteUser);
            userRepository.delete(deleteUser);
        });
    }

    @Test
    public void test_getUserProfile_whenCorrectUsernameIsGiven_returnsUser() throws Exception {
        // Assuming the user is created in the setUp method
        mockMvc.perform(get("/api/v1/user/{username}", "john")
                .header("Authorization", "Bearer abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    public void test_getUserProfile_whenCorrectUserIdIsGiven_returnsUser() throws Exception {
        User user = userRepository.findByUsername("john").get();
        mockMvc.perform(get("/api/v1/user?userId={userId}", user.getId())
                        .header("Authorization", "Bearer abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    public void test_getAllProfiles_whenUsersExist_returnsUsersList() throws Exception {
        User user = userRepository.findByUsername("john").get();

        User user2 = new User();
        user2.setUsername("john2");
        user2.setEmail("john2@example.com");
        user2.setFirstName("John2");
        user2.setLastName("Doe");
        user2.setPassword("password");
        userService.createUserProfile(user);
        userService.createUserProfile(user2);

        mockMvc.perform(get("/api/v1/users")
                .header("Authorization", "Bearer abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("john"))
                .andExpect(jsonPath("$[1].username").value("john2"));
    }

    @Test
    public void test_createUser_whenValidUserIsGiven_returnsCreatedUser() throws Exception {
        String userJson = """
            {
                "username": "newuser",
                "password": "password123",
                "email": "newuser@example.com",
                "firstName": "Test",
                "lastName": "User"
            }
            """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .header("Authorization", "Bearer abc123"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"));
    }

    @Test
    public void test_updateProfile_whenValidUserIdIsGiven_returnsUpdatedUser() throws Exception {
         String updatedUserJson = """
            {
                "username": "john_doe",
                "password": "password123",
                "email": "john.doe@newdomain.com",
                "firstName": "John",
                "lastName": "Doe"
            }
            """;
        User user = userRepository.findByUsername("john").get();
        mockMvc.perform(patch("/api/v1/user?userId={userId}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUserJson)
                        .header("Authorization", "Bearer abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }


    @Test
    public void test_updateProfile_whenValidUsernameIsGiven_returnsUpdatedUser() throws Exception {
        String updatedUserJson = """
            {
                "username": "john_doe",
                "password": "password123",
                "email": "john.doe@newdomain.com",
                "firstName": "John",
                "lastName": "Doe"
            }
            """;

        mockMvc.perform(patch("/api/v1/user/{username}", "john")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson)
                        .header("Authorization", "Bearer abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

//    @Test
//    public void test_deleteProfile_whenValidUserIdIsGiven_returnsDeletedUser() throws Exception {
//        User user = userRepository.findByUsername("john").get();
//        List<Token> tokens = tokenRepository.findByUser(user);
//        mockMvc.perform(delete("/api/v1/user?userId={userId}", user.getId())
//                .header("Authorization", "Bearer abc123"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.username").value("john"));
//
//        userRepository.save(user);
//        for(Token t : tokens)
//            tokenRepository.save(t);
//    }
//
//    @Test
//    public void test_deleteProfile_whenValidUsernameIsGiven_returnsDeletedUser() throws Exception {
//        User user = userRepository.findByUsername("john").get();
//        mockMvc.perform(delete("/api/v1/user/{username}", "john")
//                .header("Authorization", "Bearer abc123"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.username").value("john"));
//        userRepository.save(user);
//    }

}

