package com.lakshya.hexaRide.dto;

import com.lakshya.hexaRide.enums.VehicleType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCreateDTO {

    @NotNull(message = "Driver ID is required")
    private Long driverId;

    @NotBlank(message = "Vehicle number is required")
    @Size(min = 5, max = 20)
    private String vehicleNumber;

    @NotBlank(message = "Vehicle model is required")
    private String model;

    private String color;

    @NotNull(message = "Vehicle type is required (SEDAN, SUV, LUXURY, BIKE)")
    private VehicleType vehicleType;
}

