package com.arun.user_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arun.user_service.controllers.exceptions.UserNotFoundException;
import com.arun.user_service.models.User;
import com.arun.user_service.services.IUserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{username}")
    public User getUser(@PathVariable String username) throws UserNotFoundException{
        return userService.getUserProfile(username);
    }
}
