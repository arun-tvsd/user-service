package com.arun.user_service.services;

import com.arun.user_service.controllers.exceptions.UserNotFoundException;
import com.arun.user_service.models.User;

import jakarta.transaction.Transactional;

// import com.arun.user_service.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceImplTest {

    // @MockitoBean
    // private IUserService mockUserService;
    @Autowired
    private IUserService userService;
    // @Autowired
    // private UserRepository userRepository;

    // @Test
    // void mockTest_getUserProfile_whenCorrectUsernameIsGiven_returnsUser() {
    // // Arrange
    // User user = new User();
    // user.setUsername("test_test");
    // user.setFirstName("Test");
    // user.setLastName("Test");

    // when(mockUserService.getUserProfile("test_test")).thenReturn(user);

    // // Act
    // User getUser = mockUserService.getUserProfile("test_test");

    // // Assert
    // assertNotNull(getUser);
    // assertEquals(user, getUser);
    // assertEquals(user.getUsername(), getUser.getUsername());
    // assertEquals(user.getFirstName(), getUser.getFirstName());
    // assertEquals(user.getLastName(), getUser.getLastName());
    // }

    @Test
    @Transactional
    void test_getUserProfile_whenCorrectUsernameIsGiven_returnsUser() {
        // Arrange
        User user = new User();
        user.setUsername("test_test");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setPassword("test_Pass");

        // Create the user in the database
        User savedUser = userService.createUserProfile(user);

        // Act
        User getUser;
        try {
            getUser = userService.getUserProfile("test_test");

            // Assert
            assertNotNull(getUser, "User should not be null");
            assertNotNull(getUser.getId(), "User ID should not be null");
            assertEquals(savedUser.getUsername(), getUser.getUsername());
            assertEquals(savedUser.getFirstName(), getUser.getFirstName());
            assertEquals(savedUser.getLastName(), getUser.getLastName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Transactional
    void test_getUserProfile_whenIncorrectUsernameIsGiven_returnsUserNotFoundException() {
        // Arrange
        User user = new User();
        user.setUsername("test_test");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setPassword("test_Pass");

        // Create the user in the database
        userService.createUserProfile(user);

        // Act // Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserProfile("test123"));

    }

    @Test
    void testGetUserProfile() {
    }

    @Test
    void createUserProfile() {
    }

    @Test
    void updateUserProfile() {
    }

    @Test
    void testUpdateUserProfile() {
    }

    @Test
    void deleteUserProfile() {
    }
}