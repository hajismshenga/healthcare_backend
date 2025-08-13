package com.healthcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9/.-]+$", message = "Username can only contain letters, numbers, slash (/), dot (.), and dash (-)")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password is required")
    private String password; // Storing in plain text (NOT RECOMMENDED for production)

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(HOSPITAL|DOCTOR|LAB)$", message = "Invalid role. Must be HOSPITAL, DOCTOR, or LAB")
    private String role;


}
