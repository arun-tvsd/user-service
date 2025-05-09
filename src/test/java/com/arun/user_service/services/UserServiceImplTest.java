package com.arun.user_service.services;

import com.arun.user_service.controllers.exceptions.UserNotFoundException;
import com.arun.user_service.models.User;
import com.arun.user_service.repositories.UserRepository;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceImplTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private UserRepository userRepository;

    User user = new User();

    @BeforeAll
    void setup() {
        user.setUsername("test_test");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setEmail("test@mail.com");
        user.setPassword("test_Pass");

        // Create the user in the database
        user = userService.createUserProfile(user);
    }

    @AfterAll
    void cleanUp() {
        userRepository.delete(user);
    }

    @Test
    void test_getUserProfile_whenCorrectUsernameIsGiven_returnsUser() {
        // Arrange
        /*
         * User with id 1 is already present in DB.
         * username : test_test
         * firstName : Test
         * lastName: Test
         */

        // Act
        User getUser;
        try {
            getUser = userService.getUserProfile("test_test");

            // Assert
            assertNotNull(getUser, "User should not be null");
            assertNotNull(getUser.getId(), "User ID should not be null");
            assertEquals("test_test", getUser.getUsername());
            assertEquals("Test", getUser.getFirstName());
            assertEquals("Test", getUser.getLastName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void test_getUserProfile_whenIncorrectUsernameIsGiven_returnsUserNotFoundException() {
        // Arrange
        /*
         * User with id 1 is already present in DB.
         * username : test_test
         * firstName : Test
         * lastName: Test
         */

        // Act // Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserProfile("test123"));

    }

    @Test
    void test_getUserProfile_whenCorrectUserIdIsGiven_returnsUser() {
        // Arrange
        /*
         * User with id 1 is already present in DB.
         * username : test_test
         * firstName : Test
         * lastName: Test
         */

        // Act
        User getUser;
        try {
            getUser = userService.getUserProfile(1L);

            // Assert
            assertNotNull(getUser, "User should not be null");
            assertNotNull(getUser.getId(), "User ID should not be null");
            assertEquals(1L, getUser.getId());
            assertEquals("test_test", getUser.getUsername());
            assertEquals("Test", getUser.getFirstName());
            assertEquals("Test", getUser.getLastName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void test_getUserProfile_whenInCorrectUserIdIsGiven_returnsUserNotFoundException() {
        // Arrange
        /*
         * User with id 1 is already present in DB.
         * username : test_test
         * firstName : Test
         * lastName: Test
         */

        // Act // Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserProfile(5L));
    }

    @Test
    void test_createUserProfile_whenCorrectUserIsGiven_returnsUser() {
        // Arrange
        User user = new User();
        user.setUsername("test_test2");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setEmail("test2@mail.com");
        user.setPassword("test_Pass");

        // Act
        User newUser = userService.createUserProfile(user);

        // Assert
        assertNotNull(newUser, "User should not be null");
        assertNotNull(newUser.getId(), "User ID should not be null");
        assertEquals(user.getUsername(), newUser.getUsername());
        assertEquals(user.getFirstName(), newUser.getFirstName());
        assertEquals(user.getLastName(), newUser.getLastName());

    }

    @Test
    void test_createUserProfile_whenAlreadyPresentUsernameIsGiven_returnsIllegalArgumentException() {
        // Arrange
        User user = new User();
        user.setUsername("test_test");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setEmail("test@mail.com");
        user.setPassword("test_Pass");

        // Act
        // Assert
        Exception ex = assertThrows(IllegalArgumentException.class, () -> userService.createUserProfile(user));
        assertEquals("Username or email is already present!", ex.getMessage());
    }

    @Test
    void test_createUserProfile_whenAlreadyPresentEmailIsGiven_returnsIllegalArgumentException() {
        // Arrange
        User user = new User();
        user.setUsername("test_test5");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setEmail("test@mail.com");
        user.setPassword("test_Pass");

        // Act
        // Assert
        Exception ex = assertThrows(IllegalArgumentException.class, () -> userService.createUserProfile(user));
        assertEquals("Username or email is already present!", ex.getMessage());
    }

    @Test
    void test_updateUserProfile_whenPresentUserIsUpdatedWithUsername_returnsUpdatedUser() {
        // Arrange
        user.setFirstName("TestUpdate");

        // Act
        User updatedUser;
        try {
            updatedUser = userService.updateUserProfile(user.getUsername(), user);

            // Assert
            assertNotNull(updatedUser, "Updated user should not be null");
            assertNotNull(updatedUser.getId());
            assertEquals(user.getId(), updatedUser.getId());
            assertEquals(user.getUsername(), updatedUser.getUsername());
            assertNotEquals("Test", updatedUser.getFirstName());
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void test_updateUserProfile_whenNotPresentUserIsUpdatedWithUsername_returnsUserNotFoundException() {
        // Arrange
        User notPresentUser = new User();
        notPresentUser.setId(5L);
        notPresentUser.setUsername("test_test_not");
        notPresentUser.setFirstName("Test");
        notPresentUser.setLastName("Test");
        notPresentUser.setEmail("test_not@mail.com");
        notPresentUser.setPassword("test_Pass");

        // Act // Assert
        Exception ex = assertThrows(UserNotFoundException.class,
                () -> userService.updateUserProfile(notPresentUser.getUsername(), user));
        assertEquals("User not found!", ex.getMessage());
    }

    @Test
    void test_updateUserProfile_whenPresentUserIsUpdatedWithUserId_returnsUpdateduser() {
        // Arrange
        user.setFirstName("TestUpdate");

        // Act
        User updatedUser;
        try {
            updatedUser = userService.updateUserProfile(user.getId(), user);

            // Assert
            assertNotNull(updatedUser, "Updated user should not be null");
            assertNotNull(updatedUser.getId());
            assertEquals(user.getId(), updatedUser.getId());
            assertEquals(user.getUsername(), updatedUser.getUsername());
            assertNotEquals("Test", updatedUser.getFirstName());
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void test_updateUserProfile_whenNotPresentUserIsUpdatedWithUserId_returnsUserNotFoundException() {
        // Arrange
        User notPresentUser = new User();
        notPresentUser.setId(5L);
        notPresentUser.setUsername("test_test_not");
        notPresentUser.setFirstName("Test");
        notPresentUser.setLastName("Test");
        notPresentUser.setEmail("test_not@mail.com");
        notPresentUser.setPassword("test_Pass");

        // Act // Assert
        Exception ex = assertThrows(UserNotFoundException.class,
                () -> userService.updateUserProfile(notPresentUser.getId(), user));
        assertEquals("User not found!", ex.getMessage());
    }

    @Test
    void test_deleteUserProfile_whenPresentUserIsDeleteWithUserId_returnsDeletedUser() {
        // Arrange

        // Act
        User deletedUser;
        try {
            deletedUser = userService.deleteUserProfile(user.getId());

            // Assert
            assertNotNull(deletedUser, "User should not be null");
            assertNotNull(deletedUser.getId(), "User ID should not be null");
            assertEquals("test_test", deletedUser.getUsername());
            assertEquals("Test", deletedUser.getFirstName());
            assertEquals("Test", deletedUser.getLastName());
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void test_deleteUserProfile_whenNotPresentUserIsDeleteWithUserId_returnsUserNotFoundException() {
        // Arrange
        User notPresentUser = new User();
        notPresentUser.setId(5L);
        notPresentUser.setUsername("test_test_not");
        notPresentUser.setFirstName("Test");
        notPresentUser.setLastName("Test");
        notPresentUser.setEmail("test_not@mail.com");
        notPresentUser.setPassword("test_Pass");

        // Act // Assert
        Exception ex = assertThrows(UserNotFoundException.class,
                () -> userService.deleteUserProfile(notPresentUser.getId()));
        assertEquals("User not found!", ex.getMessage());

    }

    @Test
    void test_deleteUserProfile_whenPresentUserIsDeleteWithUsername_returnsDeletedUser() {
        // Arrange

        // Act
        User deletedUser;
        try {
            deletedUser = userService.deleteUserProfile(user.getUsername());

            // Assert
            assertNotNull(deletedUser, "User should not be null");
            assertNotNull(deletedUser.getId(), "User ID should not be null");
            assertEquals("test_test", deletedUser.getUsername());
            assertEquals("Test", deletedUser.getFirstName());
            assertEquals("Test", deletedUser.getLastName());
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void test_deleteUserProfile_whenNotPresentUserIsDeleteWithUsername_returnsUserNotFoundException() {
        // Arrange
        User notPresentUser = new User();
        notPresentUser.setId(5L);
        notPresentUser.setUsername("test_test_not");
        notPresentUser.setFirstName("Test");
        notPresentUser.setLastName("Test");
        notPresentUser.setEmail("test_not@mail.com");
        notPresentUser.setPassword("test_Pass");

        // Act // Assert
        Exception ex = assertThrows(UserNotFoundException.class,
                () -> userService.deleteUserProfile(notPresentUser.getUsername()));
        assertEquals("User not found!", ex.getMessage());

    }

}