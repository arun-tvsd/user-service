package com.arun.user_service.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.arun.user_service.controllers.exceptions.ExpiredTokenException;
import com.arun.user_service.controllers.exceptions.InvalidTokenException;
import com.arun.user_service.models.Token;
import com.arun.user_service.models.User;
import com.arun.user_service.repositories.TokenRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenRepository tokenRepository;

    public TokenAuthenticationFilter(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Skip token validation for /auth/** routes (e.g. /auth/login, /auth/signup)
        String path = request.getRequestURI();
        if (path.equals("/auth/login") || path.equals("/auth/signup")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract token from Authorization header
        String header = request.getHeader("Authorization");
        String tokenValue = extractToken(header);

        // Look for the token in the database
        Optional<Token> tokenOpt = tokenRepository.findByToken(tokenValue);
        if (tokenOpt.isPresent()) {
            Token token = tokenOpt.get();

            // Check if the token has expired
            if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new ExpiredTokenException("Token expired");
            }

            // Set authentication in security context
            User user = token.getUser();
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), null, List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } else {
            throw new InvalidTokenException("Invalid token");
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    // Extract the token from the Authorization header
    public static String extractToken(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new InvalidTokenException("Missing or malformed Authorization header");
        }
        return header.substring(7); // Remove "Bearer " prefix
    }
}
