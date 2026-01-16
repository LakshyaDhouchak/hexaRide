package com.lakshya.hexaRide.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleUpdateDTO {
    
    private String vehicleNumber;
    private String model;
    private String color;
}
