package com.smart_assignment_planner.planner_backend.service;

import com.smart_assignment_planner.planner_backend.model.User;
import com.smart_assignment_planner.planner_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    // Create new user
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Update existing user
    public Optional<User> updateUser(Integer id, User updated) {
        return userRepository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setEmail(updated.getEmail());
            existing.setPasswordHash(updated.getPasswordHash());
            existing.setCourses(updated.getCourses());
            return userRepository.save(existing);
        });
    }

    // Delete user
    public boolean deleteUser(Integer id) {
        return userRepository.findById(id).map(u -> {
            userRepository.delete(u);
            return true;
        }).orElse(false);
    }
}