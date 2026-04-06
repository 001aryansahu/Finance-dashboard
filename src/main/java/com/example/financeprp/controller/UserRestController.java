package com.example.financeprp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.financeprp.Service.UserService;
import com.example.financeprp.model.User;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> users = userService.getAllUsers().stream()
                .map(this::toSafeUser)
                .toList();
        return ResponseEntity.ok(users);
    }

    private Map<String, Object> toSafeUser(User user) {
        return Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "role", user.getRole(),
                "active", user.isActive()
        );
    }
}
