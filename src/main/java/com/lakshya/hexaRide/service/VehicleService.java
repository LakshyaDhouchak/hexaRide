package com.lakshya.hexaRide.service;

import com.lakshya.hexaRide.dto.VehicleCreateDTO;
import com.lakshya.hexaRide.dto.VehicleResponseDTO;
import com.lakshya.hexaRide.dto.VehicleUpdateDTO;

public interface VehicleService {
    // define the mehord
    VehicleResponseDTO registerVehicle(VehicleCreateDTO vehicleDTO);
    void deleteVehicle(Long id);
    VehicleResponseDTO vehicleGetById(Long id);
    VehicleResponseDTO updateVehicle(Long id , VehicleUpdateDTO vehicleDTO);
    
} 
