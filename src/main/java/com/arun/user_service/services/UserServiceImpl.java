package com.arun.user_service.services;

import com.arun.user_service.controllers.exceptions.UserNotFoundException;
import com.arun.user_service.models.User;
import com.arun.user_service.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService{
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserProfile(String username) throws UserNotFoundException {
        User getUser = userRepository.findByUsername(username);
        if(getUser == null) throw new UserNotFoundException("User not found!");
        return getUser;
    }

    @Override
    public User getUserProfile(Long userId) {
        User getUser = userRepository.findById(userId).get();
        System.out.println("\n\nGot User: "+getUser.getId());
        return getUser;
    }

    @Override
    public User createUserProfile(User user) {
        User newUser = userRepository.save(user);
        System.out.println("\n\nSaved User: "+newUser.getId());
        return newUser;
    }

    @Override
    public User updateUserProfile(Long userId, User user) {
        return null;
    }

    @Override
    public User updateUserProfile(String username, User user) {
        return null;
    }

    @Override
    public User deleteUserProfile(Long userId) {
        return null;
    }
}
