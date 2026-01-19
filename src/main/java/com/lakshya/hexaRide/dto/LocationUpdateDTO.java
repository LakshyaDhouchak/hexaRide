package com.lakshya.hexaRide.dto;

import lombok.Data;

@Data
public class LocationUpdateDTO {
    // define the attriute
    private Long driverId;
    private Long TripId;
    private Double lat;
    private Double lng; 
}
