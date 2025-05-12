package com.arun.user_service.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    
    @BeforeEach
    void setUp() {
        // Clean up the database before each test if needed
        userRepository.deleteAll();
    }

    @Test
    public void test_getUserProfile_whenCorrectUsernameIsGiven_returnsUser() throws Exception {
        User user = new User("john_doe", "John", "Doe", "john.doe@example.com");
        userService.createUserProfile(user); // Create a real user in the database

        mockMvc.perform(get("/api/v1/user/{username}", "john_doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    public void test_getUserProfile_whenCorrectUserIdIsGiven_returnsUser() throws Exception {
        User user = new User("john_doe", "John", "Doe", "john.doe@example.com");
        User createdUser = userService.createUserProfile(user); // Create a real user in the database

        mockMvc.perform(get("/api/v1/user?userId={userId}", createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    public void test_getAllProfiles_whenUsersExist_returnsUsersList() throws Exception {
        User user1 = new User("john_doe", "John", "Doe", "john.doe@example.com");
        User user2 = new User("jane_doe", "Jane", "Doe", "jane.doe@example.com");
        userService.createUserProfile(user1);
        userService.createUserProfile(user2);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("john_doe"))
                .andExpect(jsonPath("$[1].username").value("jane_doe"));
    }

    @Test
    public void test_createUser_whenValidUserIsGiven_returnsCreatedUser() throws Exception {
        String userJson = "{\"username\":\"john_doe\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\"}";

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    public void test_updateProfile_whenValidUserIdIsGiven_returnsUpdatedUser() throws Exception {
        User user = new User("john_doe", "John", "Doe", "john.doe@example.com");
        User createdUser = userService.createUserProfile(user); // Create a real user in the database
        String updatedUserJson = "{\"username\":\"john_doe\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@newdomain.com\"}";

        mockMvc.perform(put("/api/v1/user?userId={userId}", createdUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@newdomain.com"));
    }

    @Test
    public void test_updateProfile_whenValidUsernameIsGiven_returnsUpdatedUser() throws Exception {
        User user = new User("john_doe", "John", "Doe", "john.doe@example.com");
        userService.createUserProfile(user); // Create a real user in the database
        String updatedUserJson = "{\"username\":\"john_doe\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@newdomain.com\"}";

        mockMvc.perform(put("/api/v1/user/{username}", "john_doe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@newdomain.com"));
    }

    @Test
    public void test_deleteProfile_whenValidUserIdIsGiven_returnsDeletedUser() throws Exception {
        User user = new User("john_doe", "John", "Doe", "john.doe@example.com");
        User createdUser = userService.createUserProfile(user); // Create a real user in the database

        mockMvc.perform(delete("/api/v1/user?userId={userId}", createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"));
    }

    @Test
    public void test_deleteProfile_whenValidUsernameIsGiven_returnsDeletedUser() throws Exception {
        User user = new User("john_doe", "John", "Doe", "john.doe@example.com");
        userService.createUserProfile(user); // Create a real user in the database

        mockMvc.perform(delete("/api/v1/user/{username}", "john_doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"));
    }
}

