package com.recyclewise.service;

import com.recyclewise.model.User;

/**
 * SOLID — (I) ISP: focused only on user account operations
 * SOLID — (D) DIP: controllers depend on this abstraction
 */
public interface UserService {
    User register(String username, String email, String password, String fullName);
    User login(String username, String password);
    User findById(Long id);
    User findByUsername(String username);
    void addPoints(User user, int points);
    void deductPoints(User user, int points);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
