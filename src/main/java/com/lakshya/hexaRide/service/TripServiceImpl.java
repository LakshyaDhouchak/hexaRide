package com.lakshya.hexaRide.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakshya.hexaRide.dto.TripRequestDTO;
import com.lakshya.hexaRide.dto.TripResponseDTO;
import com.lakshya.hexaRide.entity.Trip;
import com.lakshya.hexaRide.entity.User;
import com.lakshya.hexaRide.enums.TripStatus;
import com.lakshya.hexaRide.exceptionHandling.InvalidCredentialException;
import com.lakshya.hexaRide.exceptionHandling.ResourceNotFoundException;
import com.lakshya.hexaRide.repository.TripRepository;
import com.lakshya.hexaRide.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {
    // define the properties
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final UserService userService;
    private TripResponseDTO mapToResponseDTO(Trip trip){
        return TripResponseDTO.builder()
                .id(trip.getId())
                .status(trip.getStatus())
                .pickupLat(trip.getPickupLat())
                .pickupLng(trip.getPickupLng())
                .dropoffLat(trip.getDropoffLat())
                .dropoffLng(trip.getDropoffLng())
                .completedAt(trip.getCompletedAt())
                .distanceKm(trip.getDistanceKm())
                .fare(trip.getFare())
                .startedAt(trip.getStartedAt())
                .driver(trip.getDriver()!=null ? userService.mapToResponseDTO(trip.getDriver()) : null)
                .rider(userService.mapToResponseDTO(trip.getRider()))
                .build();
    }
    private final List<TripStatus> activeStatuses = Arrays.asList(
            TripStatus.REQUESTED, TripStatus.ACCEPTED, TripStatus.STARTED
    );

    @Transactional
    @Override
    public TripResponseDTO requestTrip(TripRequestDTO tripDTO) {
        User rider = userRepository.findById(tripDTO.getRiderId())
                .orElseThrow(() -> new ResourceNotFoundException("Rider not found"));

        if (tripRepository.existsByRiderAndStatusIn(rider, activeStatuses)) {
            throw new InvalidCredentialException("You already have an active ride request.");
        }

        double distance = calculateHaversineDistance(
                tripDTO.getPickupLat(), tripDTO.getPickupLng(),
                tripDTO.getDropoffLat(), tripDTO.getDropoffLng()
        );

        double baseFare = 10.0;
        double perKmRate = 2.0;
        double calculatedFare = baseFare + (distance * perKmRate);

        Trip trip = Trip.builder()
                .rider(rider)
                .pickupLat(tripDTO.getPickupLat())
                .pickupLng(tripDTO.getPickupLng())
                .dropoffLat(tripDTO.getDropoffLat())
                .dropoffLng(tripDTO.getDropoffLng())
                .distanceKm(distance) 
                .fare(calculatedFare)  
                .status(TripStatus.REQUESTED)
                .build();

        return mapToResponseDTO(tripRepository.save(trip));
    }

    @Transactional
    @Override
    public TripResponseDTO acceptTrip(Long tripId, Long driverId) {
        Trip trip = findTripById(tripId);
        // define the condition
        if(trip.getStatus() != TripStatus.REQUESTED){
            throw new InvalidCredentialException("This trip is no longer available.");
        }
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        if (tripRepository.existsByDriverAndStatusIn(driver, activeStatuses)) {
            throw new InvalidCredentialException("Driver is already on another active trip.");
        }

        trip.setDriver(driver);
        trip.setStatus(TripStatus.ACCEPTED);
        return mapToResponseDTO(tripRepository.save(trip));        
    }

    @Transactional
    @Override
    public TripResponseDTO startTrip(Long tripId) {
        // define the condition
        Trip trip = findTripById(tripId);
        if(trip.getStatus() != TripStatus.ACCEPTED){
            throw new InvalidCredentialException("Trip cannot start until it is accepted.");
        }
        trip.setStatus(TripStatus.STARTED);
        trip.setCompletedAt(LocalDateTime.now());
        return mapToResponseDTO(trip);
    }

    @Transactional
    @Override
    public TripResponseDTO completeTrip(Long tripId) {
        // define the condition
        Trip trip = findTripById(tripId);
        if(trip.getStatus() != TripStatus.IN_PROGRESS){
            throw new InvalidCredentialException("Cannot complete a trip that hasn't started.");
        }
        trip.setStatus(TripStatus.COMPLETED);
        trip.setCompletedAt(LocalDateTime.now());
        return mapToResponseDTO(trip);
    }

    @Transactional
    @Override
    public TripResponseDTO cancelTrip(Long tripId) {
        // define the condition
        Trip trip = findTripById(tripId);
        if(trip.getStatus() == TripStatus.COMPLETED || trip.getStatus() == TripStatus.CANCELLED){
            throw new InvalidCredentialException("Trip cannot be cancelled from its current state: " + trip.getStatus());
        }
        trip.setStatus(TripStatus.CANCELLED);
        return mapToResponseDTO(trip);
    }

    @Transactional(readOnly = true)
    @Override
    public TripResponseDTO getTripById(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                    .orElseThrow(()-> new ResourceNotFoundException("Trip not found with id: " + tripId));
        return mapToResponseDTO(trip);            
    }

    // define the helper class
    private Trip findTripById(Long id){
        return tripRepository.findById(id)
            .orElseThrow(()-> new ResourceNotFoundException("Trip not found with id: " + id));
    }
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; 

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; 
    }
    
}
