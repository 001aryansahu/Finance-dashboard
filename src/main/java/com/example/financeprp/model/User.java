package com.example.financeprp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "userdata")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters.")
    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @NotBlank(message = "Password is required.")
    @Size(min = 4, max = 100, message = "Password must be between 4 and 100 characters.")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Role is required.")
    @Pattern(regexp = "VIEWER|ANALYST|ADMIN", message = "Role must be VIEWER, ANALYST, or ADMIN.")
    @Column(nullable = false, length = 20)
    private String role;

    private boolean active = true;
}
