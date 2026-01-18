package com.lakshya.hexaRide.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakshya.hexaRide.dto.UserCreateDTO;
import com.lakshya.hexaRide.dto.UserResponseDTO;
import com.lakshya.hexaRide.dto.UserUpdateDTO;
import com.lakshya.hexaRide.entity.User;
import com.lakshya.hexaRide.exceptionHandling.ResourceAlreadyExistsException;
import com.lakshya.hexaRide.exceptionHandling.ResourceNotFoundException;
import com.lakshya.hexaRide.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    // define the properties
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserResponseDTO mapToResponseDTO(User user){
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .rating(user.getRating())
                .createdAt(user.getCreatedAt())
                .build();

    }

    @Override
    @Transactional
    public UserResponseDTO signUp(UserCreateDTO userDTO) {
        // define the condition
        if(userRepository.existsByEmail(userDTO.getEmail())){
            throw new ResourceAlreadyExistsException("Account with email " + userDTO.getEmail() + " already exists.");
        }
        User user = User.builder()
                    .name(userDTO.getName())
                    .email(userDTO.getEmail())
                    .phone(userDTO.getPhone())
                    .passwordHash(passwordEncoder.encode(userDTO.getPassward()))
                    .role(userDTO.getRole())
                    .build();
        return mapToResponseDTO(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getProfile(Long id) {
        User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return mapToResponseDTO(user);            
    }

    @Override
    @Transactional
    public UserResponseDTO updateProfile(Long id, UserUpdateDTO userDTO) {
        // define the condition
        User user = userRepository.findById(id)
                    .orElseThrow(()-> new ResourceNotFoundException("Update failed: User not found."));

        if(userDTO.getName()!=null){
            user.setName(userDTO.getName());
        }    
        if(userDTO.getPhone()!=null){
            user.setPhone(userDTO.getPhone());
        }  
        
        return mapToResponseDTO(userRepository.save(user));    
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        // define the condition
        if(!userRepository.existsById(id)){
            throw new ResourceNotFoundException("Delete failed: User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
}
