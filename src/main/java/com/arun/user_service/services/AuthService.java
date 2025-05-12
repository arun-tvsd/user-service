package com.arun.user_service.services;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.arun.user_service.models.Token;
import com.arun.user_service.models.User;
import com.arun.user_service.repositories.TokenRepository;
import com.arun.user_service.repositories.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public AuthService(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public ResponseEntity<?> signup(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    public ResponseEntity<?> login(Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && BCrypt.checkpw(password, userOpt.get().getPassword())) {
            User user = userOpt.get();
            String tokenStr = UUID.randomUUID().toString();

            Token token = new Token();
            token.setToken(tokenStr);
            token.setUser(user);
            token.setCreatedAt(LocalDateTime.now());
            token.setExpiresAt(LocalDateTime.now().plusHours(24));
            tokenRepository.save(token);

            return ResponseEntity.ok(Map.of("token", tokenStr));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    public ResponseEntity<?> verify(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Missing or invalid token"));
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "email", user.getEmail(),
                "firstName", user.getFirstName()));
    }

    public ResponseEntity<?> logout(String tokenValue) {
        Optional<Token> tokenOpt = tokenRepository.findByToken(tokenValue);
        if (tokenOpt.isPresent()) {
            tokenRepository.delete(tokenOpt.get());
            return ResponseEntity.ok("Logged out successfully");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }
}

