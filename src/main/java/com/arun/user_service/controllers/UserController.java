package com.arun.user_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arun.user_service.controllers.exceptions.UserNotFoundException;
import com.arun.user_service.models.User;
import com.arun.user_service.services.IUserService;

import java.util.List;

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
    public User getProfile(@PathVariable String username) throws UserNotFoundException{
        return userService.getUserProfile(username);
    }
    @GetMapping("/user")
    public User getProfile(@RequestParam Long userId) throws UserNotFoundException{
        return userService.getUserProfile(userId);
    }
    @GetMapping("/users")
    public List<User> getAllProfile() {
        return userService.getAllUserProfile();
    }
    @PostMapping("/users")
    public User createProfile(@RequestBody User user) {
        return userService.createUserProfile(user);
    }

    @PutMapping("/user")
    public User updateProfile (@RequestParam Long userId, @RequestBody User user) throws UserNotFoundException {
        return userService.updateUserProfile(userId, user);
    }
    @PutMapping("/user/{username}")
    public User updateProfile (@PathVariable String username, @RequestBody User user) throws UserNotFoundException {
        return userService.updateUserProfile(username, user);
    }

    @DeleteMapping("/user")
    public User deleteProfile(@RequestParam Long userId) throws UserNotFoundException{
        return userService.deleteUserProfile(userId);
    }
    @DeleteMapping("/user/{username}")
    public User deleteProfile(@PathVariable String username) throws UserNotFoundException{
        return userService.deleteUserProfile(username);
    }


}
