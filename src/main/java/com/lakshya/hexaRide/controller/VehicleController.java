package com.lakshya.hexaRide.controller;

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

import com.lakshya.hexaRide.dto.VehicleCreateDTO;
import com.lakshya.hexaRide.dto.VehicleResponseDTO;
import com.lakshya.hexaRide.dto.VehicleUpdateDTO;
import com.lakshya.hexaRide.service.VehicleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {
    // define the properties
    private final VehicleService vehicleService;

    // define the methord
    @PostMapping("/regiserVehicle")
    public ResponseEntity<VehicleResponseDTO> RegisterVehicle(@Valid @RequestBody VehicleCreateDTO vehicleDTO){
        VehicleResponseDTO getRegiserVehile = vehicleService.registerVehicle(vehicleDTO);
        return new ResponseEntity<>(getRegiserVehile,HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> getVehicleById(@PathVariable Long id){
        VehicleResponseDTO getVehicle = vehicleService.vehicleGetById(id);
        return ResponseEntity.ok(getVehicle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> updateVehicle(@PathVariable Long id , @RequestBody VehicleUpdateDTO vehicleDTO){
        VehicleResponseDTO updateVehicle = vehicleService.updateVehicle(id,vehicleDTO);
        return ResponseEntity.ok(updateVehicle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id){
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
