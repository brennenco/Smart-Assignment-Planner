package com.smart_assignment_planner.planner_backend.service;

import com.smart_assignment_planner.planner_backend.model.User;
import com.smart_assignment_planner.planner_backend.model.UserRole;
import com.smart_assignment_planner.planner_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User register(String name, String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(UserRole.USER);
        return userRepository.save(user);
    }

    @Transactional
    public Optional<User> updateUserFields(Integer id, String name, String email, UserRole role) {
        return userRepository.findById(id).map(existing -> {
            if (name != null) {
                existing.setName(name);
            }
            if (email != null) {
                existing.setEmail(email);
            }
            if (role != null) {
                existing.setRole(role);
            }
            return userRepository.save(existing);
        });
    }

    @Transactional
    public boolean deleteUser(Integer id) {
        return userRepository.findById(id).map(u -> {
            userRepository.delete(u);
            return true;
        }).orElse(false);
    }
}
