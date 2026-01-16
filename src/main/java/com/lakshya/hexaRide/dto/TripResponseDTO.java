package com.lakshya.hexaRide.dto;

import com.lakshya.hexaRide.enums.TripStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripResponseDTO {
    private Long id;
    
    private UserResponseDTO rider;
    private UserResponseDTO driver; 

    private TripStatus status;
    private Double pickupLat;
    private Double pickupLng;
    private Double dropoffLat;
    private Double dropoffLng;
    
    private Double fare;
    private Double distanceKm;

    private LocalDateTime requestedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
