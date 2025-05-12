package com.arun.user_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arun.user_service.controllers.exceptions.UserNotFoundException;
import com.arun.user_service.models.User;
import com.arun.user_service.services.IUserService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<User> getProfile(@PathVariable String username) throws UserNotFoundException {
        User user = userService.getUserProfile(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/user")
    public ResponseEntity<User> getProfile(@RequestParam Long userId) throws UserNotFoundException {
        User user = userService.getUserProfile(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllProfile() {
        List<User> users = userService.getAllUserProfile();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUserProfile(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/user")
    public ResponseEntity<User> updateProfile(@RequestParam Long userId, @Valid @RequestBody User user)
            throws UserNotFoundException {
        User updatedUser = userService.updateUserProfile(userId, user);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/user/{username}")
    public ResponseEntity<User> updateProfile(@PathVariable String username, @Valid @RequestBody User user)
            throws UserNotFoundException {
        User updatedUser = userService.updateUserProfile(username, user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/user")
    public ResponseEntity<User> deleteProfile(@RequestParam Long userId) throws UserNotFoundException {
        User deletedUser = userService.deleteUserProfile(userId);
        return ResponseEntity.ok(deletedUser);
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<User> deleteProfile(@PathVariable String username) throws UserNotFoundException {
        User deletedUser = userService.deleteUserProfile(username);
        return ResponseEntity.ok(deletedUser);
    }

}