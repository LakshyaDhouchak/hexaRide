package com.lakshya.hexaRide.dto;

import com.lakshya.hexaRide.enums.TripStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripUpdateDTO {
    private Long driverId;
    private TripStatus status;
    private Double fare;
    private Double distanceKm;
}
