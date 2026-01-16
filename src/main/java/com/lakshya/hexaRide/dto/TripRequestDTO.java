package com.lakshya.hexaRide.dto;

import com.lakshya.hexaRide.enums.VehicleType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripRequestDTO {

    @NotNull(message = "Rider ID is required")
    private Long riderId;

    @NotNull(message = "Please select a vehicle type")
    private VehicleType preferredVehicleType;

    @NotNull(message = "Pickup Latitude is required")
    private Double pickupLat;

    @NotNull(message = "Pickup Longitude is required")
    private Double pickupLng;

    @NotNull(message = "Dropoff Latitude is required")
    private Double dropoffLat;

    @NotNull(message = "Dropoff Longitude is required")
    private Double dropoffLng;

    private String pickupAddress;
    private String dropoffAddress;

    private Double estimatedFare;
}
