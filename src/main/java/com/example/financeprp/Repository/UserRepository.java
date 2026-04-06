package com.example.financeprp.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.financeprp.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNameIgnoreCaseAndPasswordAndActiveTrue(String name, String password);

    Optional<User> findByNameIgnoreCase(String name);

    boolean existsByRole(String role);

    boolean existsByNameIgnoreCase(String name);

    long countByRole(String role);
}
