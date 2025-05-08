package com.arun.user_service.services;

import com.arun.user_service.controllers.exceptions.UserNotFoundException;
import com.arun.user_service.models.User;

public interface IUserService {
    public User getUserProfile(String username) throws UserNotFoundException;
    public User getUserProfile(Long userId);

    public User createUserProfile(User user);

    public User updateUserProfile(Long userId, User user);
    public User updateUserProfile(String username, User user);

    public User deleteUserProfile(Long userId);

}
