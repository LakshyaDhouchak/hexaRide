package com.lakshya.hexaRide.service;

import com.lakshya.hexaRide.dto.TripRequestDTO;
import com.lakshya.hexaRide.dto.TripResponseDTO;

public interface TripService {
    // define the methord
    TripResponseDTO requestTrip(TripRequestDTO tripDTO);
    TripResponseDTO acceptTrip(Long tripId , Long driverId);
    TripResponseDTO startTrip(Long tripId);
    TripResponseDTO completeTrip(Long tripId);
    TripResponseDTO cancelTrip(Long tripId);
    TripResponseDTO getTripById(Long tripId);
}
