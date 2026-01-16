package com.lakshya.hexaRide.dto;

import com.lakshya.hexaRide.enums.VehicleType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponseDTO {
    private Long id;
    private String vehicleNumber;
    private String model;
    private String color;
    private VehicleType vehicleType;
    private Long driverId;
}
