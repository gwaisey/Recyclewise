package com.recyclewise.service.impl;

import com.recyclewise.exception.ResourceNotFoundException;
import com.recyclewise.model.User;
import com.recyclewise.repository.UserRepository;
import com.recyclewise.service.UserService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OOP   — Encapsulation: password hashing hidden inside this class
 * SOLID — (S) SRP: only user account management
 * SOLID — (D) DIP: depends on UserRepository abstraction
 *
 * Note: For simplicity we store a basic hash. In production use Spring Security BCrypt.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(String username, String email, String password, String fullName) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .totalPoints(0)
                .build();
        return userRepository.save(Objects.requireNonNull(user));
    }

    @Override
    public User login(String identifier, String password) {
        User user = userRepository.findByUsername(identifier)
                .orElseGet(() -> userRepository.findByEmail(identifier)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid email/username or password")));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email/username or password");
        }
        return user;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    @Override
    @Transactional
    public void addPoints(User user, int points) {
        user.setTotalPoints(user.getTotalPoints() + points);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deductPoints(User user, int points) {
        if (user.getTotalPoints() < points) {
            throw new IllegalArgumentException("Insufficient points");
        }
        user.setTotalPoints(user.getTotalPoints() - points);
        userRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}
