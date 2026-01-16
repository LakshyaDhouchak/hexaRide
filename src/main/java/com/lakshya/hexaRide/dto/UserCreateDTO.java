package com.lakshya.hexaRide.dto;

import com.lakshya.hexaRide.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDTO {
    // define user data transfer object here

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8 , message = "Password must be at least 8 characters")
    private String passward;

    @NotBlank(message = "Phone is requird")
    private String phone;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Role must be RIDER or DRIVER")
    private Role role;
}
