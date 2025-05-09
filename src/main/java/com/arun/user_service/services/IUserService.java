package com.arun.user_service.services;

import java.util.List;

import com.arun.user_service.controllers.exceptions.UserNotFoundException;
import com.arun.user_service.models.User;

public interface IUserService {
    
    public List<User> getAllUserProfile();

    public User getUserProfile(Long userId) throws UserNotFoundException;
    public User getUserProfile(String username) throws UserNotFoundException;

    public User createUserProfile(User user);

    public User updateUserProfile(Long userId, User user) throws UserNotFoundException;
    public User updateUserProfile(String username, User user) throws UserNotFoundException;

    public User deleteUserProfile(Long userId) throws UserNotFoundException;
    public User deleteUserProfile(String username) throws UserNotFoundException;

}
