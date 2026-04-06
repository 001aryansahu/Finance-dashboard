package com.example.financeprp.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.financeprp.Repository.UserRepository;
import com.example.financeprp.exception.InvalidOperationException;
import com.example.financeprp.exception.ResourceNotFoundException;
import com.example.financeprp.model.User;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerUser(User user) {
        normalizeUser(user);
        if (userRepository.existsByNameIgnoreCase(user.getName())) {
            throw new InvalidOperationException("Username already exists.");
        }
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if ("ADMIN".equalsIgnoreCase(user.getRole()) && userRepository.countByRole("ADMIN") <= 1) {
            throw new InvalidOperationException("You cannot delete the last admin user.");
        }

        userRepository.delete(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public User updateUser(User user) {
        User existing = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        normalizeUser(user);

        Optional<User> duplicate = userRepository.findByNameIgnoreCase(user.getName());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(user.getId())) {
            throw new InvalidOperationException("Another user already uses this username.");
        }

        if ("ADMIN".equalsIgnoreCase(existing.getRole())
                && !"ADMIN".equalsIgnoreCase(user.getRole())
                && userRepository.countByRole("ADMIN") <= 1) {
            throw new InvalidOperationException("At least one admin user must remain in the system.");
        }

        existing.setName(user.getName());
        existing.setPassword(user.getPassword());
        existing.setRole(user.getRole());
        existing.setActive(user.isActive());
        return userRepository.save(existing);
    }

    public User authenticate(String name, String password) {
        return userRepository.findByNameIgnoreCaseAndPasswordAndActiveTrue(name == null ? null : name.trim(), password)
                .orElse(null);
    }

    public void createDefaultAdminIfMissing() {
        if (!userRepository.existsByRole("ADMIN")) {
            User admin = new User();
            admin.setName("admin");
            admin.setPassword("admin123");
            admin.setRole("ADMIN");
            admin.setActive(true);
            userRepository.save(admin);
        }
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    private void normalizeUser(User user) {
        user.setName(user.getName() == null ? null : user.getName().trim());
        user.setPassword(user.getPassword() == null ? null : user.getPassword().trim());
        user.setRole(user.getRole() == null ? null : user.getRole().trim().toUpperCase());
    }
}
