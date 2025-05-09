package com.arun.user_service.services;

import com.arun.user_service.controllers.exceptions.UserNotFoundException;
import com.arun.user_service.models.User;
import com.arun.user_service.repositories.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserProfile(String username) throws UserNotFoundException {
        User getUser = userRepository.findByUsername(username);
        if (getUser == null)
            throw new UserNotFoundException("User not found!");
        return getUser;
    }

    @Override
    public User getUserProfile(Long userId) throws UserNotFoundException {
        try {
            User getUser = userRepository.findById(userId).get();
            return getUser;
        } catch (NoSuchElementException e) {
            throw new UserNotFoundException("User not found!");
        }
    }

    @Override
    public User createUserProfile(User user) {
        try {
            User newUser = userRepository.save(user);
            return newUser;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Username or email is already present!");
        }
    }

    @Override
    public User updateUserProfile(Long userId, User user) throws UserNotFoundException {
        User updatedUser = getUserProfile(userId);
        
        if (user.getFirstName() != null && !user.getFirstName().equals(updatedUser.getFirstName())) {
            updatedUser.setFirstName(user.getFirstName());
        }
        
        if (user.getLastName() != null && !user.getLastName().equals(updatedUser.getLastName())) {
            updatedUser.setLastName(user.getLastName());
        }
        
        if (user.getProfilePicture() != null && !user.getProfilePicture().equals(updatedUser.getProfilePicture())) {
            updatedUser.setProfilePicture(user.getProfilePicture());
        }
              
        return userRepository.save(updatedUser);
    }

    @Override
    public User updateUserProfile(String username, User user) throws UserNotFoundException {
        User updatedUser = getUserProfile(username);

        return updateUserProfile(updatedUser.getId(), user);
    }

    @Override
    public User deleteUserProfile(Long userId) throws UserNotFoundException {
        User deleteUser = getUserProfile(userId);
        userRepository.delete(deleteUser);

        return deleteUser;
    }

    @Override
    public User deleteUserProfile(String username) throws UserNotFoundException {
        User deleteUser = getUserProfile(username);
        return deleteUserProfile(deleteUser.getId());
    }

    @Override
    public List<User> getAllUserProfile() {
        return userRepository.findAll();
    }
}
