package com.example.financeprp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "expense")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expenses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Amount is required.")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0.")
    private Double amount;

    @NotBlank(message = "Type is required.")
    @Pattern(regexp = "INCOME|EXPENSE", message = "Type must be INCOME or EXPENSE.")
    private String type;

    @NotBlank(message = "Category is required.")
    @Size(max = 50, message = "Category must not exceed 50 characters.")
    private String category;

    @NotNull(message = "Date is required.")
    private LocalDate date;

    @Size(max = 255, message = "Notes must not exceed 255 characters.")
    private String notes;
}
