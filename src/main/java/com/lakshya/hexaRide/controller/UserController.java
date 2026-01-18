package com.lakshya.hexaRide.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lakshya.hexaRide.dto.UserCreateDTO;
import com.lakshya.hexaRide.dto.UserResponseDTO;
import com.lakshya.hexaRide.dto.UserUpdateDTO;
import com.lakshya.hexaRide.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    // define the properties
    private final UserService userService;

    // define the methord
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signUp(@Valid @RequestBody UserCreateDTO userDTO){
        UserResponseDTO userSignUp = userService.signUp(userDTO);
        return new ResponseEntity<>(userSignUp, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getProfile(@PathVariable Long id){
        UserResponseDTO getUserProfile = userService.getProfile(id);
        return ResponseEntity.ok(getUserProfile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateProfile(@PathVariable Long id , @RequestBody UserUpdateDTO userDTO){
        UserResponseDTO updateUserProfile = userService.updateProfile(id,userDTO);
        return ResponseEntity.ok(updateUserProfile);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> DeleteProfile( @PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<List<UserResponseDTO>> AllProfile(){
        List<UserResponseDTO> getAllProfile = userService.getAllUsers();
        return ResponseEntity.ok(getAllProfile);
    }

}
