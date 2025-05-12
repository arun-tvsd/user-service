package com.arun.user_service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.arun.user_service.models.Token;
import com.arun.user_service.models.User;
import com.arun.user_service.repositories.TokenRepository;
import com.arun.user_service.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private TokenRepository tokenRepository;
    @InjectMocks private AuthService authService;

    @Test
    public void test_signup_whenUsernameExists_returnsConflict() {
        User user = new User();
        user.setUsername("existingUser");

        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = authService.signup(user);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void test_login_whenCorrectCredentials_returnsToken() {
        User user = new User();
        user.setUsername("john");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        Map<String, String> loginData = Map.of("username", "john", "password", "password");

        ResponseEntity<?> response = authService.login(loginData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("token"));
    }

    @Test
    public void test_verify_whenAuthenticationIsValid_returnsUserProfile() {
        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setFirstName("John");

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("john");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = authService.verify(auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((Map<?, ?>) response.getBody()).get("username").equals("john"));
    }

    @Test
    public void test_logout_whenTokenExists_deletesToken() {
        Token token = new Token();
        token.setToken("abc123");

        when(tokenRepository.findByToken("abc123")).thenReturn(Optional.of(token));

        ResponseEntity<?> response = authService.logout("abc123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(tokenRepository).delete(token);
    }
}

