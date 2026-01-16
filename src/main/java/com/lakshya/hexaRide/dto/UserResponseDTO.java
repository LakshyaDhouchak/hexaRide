package com.lakshya.hexaRide.dto;

import java.time.LocalDateTime;

import com.lakshya.hexaRide.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    // define the attribute
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private Double rating;
    private LocalDateTime createdAt;

}
