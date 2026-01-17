package com.lakshya.hexaRide.service;

import java.util.List;

import com.lakshya.hexaRide.dto.UserCreateDTO;
import com.lakshya.hexaRide.dto.UserResponseDTO;
import com.lakshya.hexaRide.dto.UserUpdateDTO;

public interface UserService {
    // define the methord
    UserResponseDTO signUp(UserCreateDTO userDTO);
    UserResponseDTO getProfile(Long id);
    UserResponseDTO updateProfile(Long id , UserUpdateDTO userDTO);
    void deleteUser(Long id);
    List<UserResponseDTO> getAllUsers();
    
} 
