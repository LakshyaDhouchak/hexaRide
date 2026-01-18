package com.lakshya.hexaRide.controller;

import com.lakshya.hexaRide.dto.TripRequestDTO;
import com.lakshya.hexaRide.dto.TripResponseDTO;
import com.lakshya.hexaRide.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor

public class TripController {
    // define the properties
    private final TripService tripService;

    // define the methord
    @PostMapping("/request")
    public ResponseEntity<TripResponseDTO> requestTrip(@Valid @RequestBody TripRequestDTO tripDTO) {
        return new ResponseEntity<>(tripService.requestTrip(tripDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/{tripId}/accept/{driverId}")
    public ResponseEntity<TripResponseDTO> acceptTrip(@PathVariable Long tripId, @PathVariable Long driverId) {
        return ResponseEntity.ok(tripService.acceptTrip(tripId, driverId));
    }

    @PatchMapping("/{tripId}/start")
    public ResponseEntity<TripResponseDTO> startTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.startTrip(tripId));
    }

    @PatchMapping("/{tripId}/complete")
    public ResponseEntity<TripResponseDTO> completeTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.completeTrip(tripId));
    }

    @PatchMapping("/{tripId}/cancel")
    public ResponseEntity<TripResponseDTO> cancelTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.cancelTrip(tripId));
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponseDTO> getTripById(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.getTripById(tripId));
    }
}
