package com.lakshya.hexaRide.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakshya.hexaRide.dto.VehicleCreateDTO;
import com.lakshya.hexaRide.dto.VehicleResponseDTO;
import com.lakshya.hexaRide.dto.VehicleUpdateDTO;
import com.lakshya.hexaRide.entity.User;
import com.lakshya.hexaRide.entity.Vehicle;
import com.lakshya.hexaRide.enums.Role;
import com.lakshya.hexaRide.exceptionHandling.InvalidCredentialException;
import com.lakshya.hexaRide.exceptionHandling.ResourceAlreadyExistsException;
import com.lakshya.hexaRide.exceptionHandling.ResourceNotFoundException;
import com.lakshya.hexaRide.repository.UserRepository;
import com.lakshya.hexaRide.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    // define the properties
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private VehicleResponseDTO mapToResponseDTO(Vehicle vehicle){
        return VehicleResponseDTO.builder()
                .id(vehicle.getId())
                .vehicleNumber(vehicle.getVehicleNumber())
                .driverId(vehicle.getDriver().getId())
                .color(vehicle.getColor())
                .model(vehicle.getModel())
                .vehicleType(vehicle.getVehicleType())
                .build();
    }

    @Override
    @Transactional
    public VehicleResponseDTO registerVehicle(VehicleCreateDTO vehicleDTO) {
        // define the condition
        User driver = userRepository.findById(vehicleDTO.getDriverId())
                    .orElseThrow(()-> new ResourceNotFoundException("Driver not found with id: " + vehicleDTO.getDriverId()));
        if(!Role.DRIVER.equals(driver.getRole())){
            throw new InvalidCredentialException("Unauthorized: Only users with the DRIVER role can register a vehicle.");
        }
        if(vehicleRepository.existsById(vehicleDTO.getDriverId())){
            throw new ResourceAlreadyExistsException("Driver already has a vehicle registered.");
        }

        Vehicle vehicle = Vehicle.builder()
                        .driver(driver)
                        .vehicleNumber(vehicleDTO.getVehicleNumber())
                        .model(vehicleDTO.getModel())
                        .color(vehicleDTO.getColor())
                        .vehicleType(vehicleDTO.getVehicleType())
                        .build();
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return mapToResponseDTO(savedVehicle);                
    }

    @Override
    @Transactional
    public void deleteVehicle(Long id) {
        // define the condition
        if(!vehicleRepository.existsById(id)){
            throw new ResourceNotFoundException("Cannot delete: Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponseDTO vehicleGetById(Long id) {
        // define the condition
        Vehicle vehicle = vehicleRepository.findByDriverId(id)
                        .orElseThrow(()-> new ResourceNotFoundException("No vehicle found for Driver ID: " + id));

        return mapToResponseDTO(vehicle);                
    }

    @Override
    @Transactional
    public VehicleResponseDTO updateVehicle(Long id, VehicleUpdateDTO vehicleDTO) {
        // define the condition
        Vehicle vehicle = vehicleRepository.findByDriverId(id)
                        .orElseThrow(()-> new ResourceNotFoundException("Vehicle not found for update."));
        if(vehicleDTO.getVehicleNumber()!= null){
            vehicle.setVehicleNumber(vehicleDTO.getVehicleNumber());
        } 
        if((vehicleDTO.getColor()!=null)){
            vehicle.setColor(vehicleDTO.getColor());
        }    
        if(vehicleDTO.getModel()!=null){
            vehicle.setModel(vehicleDTO.getModel());
        }  
               
        return mapToResponseDTO(vehicle);                
    }
    
}
