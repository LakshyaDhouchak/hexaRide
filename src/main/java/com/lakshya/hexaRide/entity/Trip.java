package com.lakshya.hexaRide.entity;

import java.time.LocalDateTime;

import com.lakshya.hexaRide.enums.TripStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trips")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Trip {
    // define trip attribute here
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rider_id", nullable = false)
    private User rider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id") 
    private User driver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status;

    private Double pickupLat;
    private Double pickupLng;
    private Double dropoffLat;
    private Double dropoffLng;

    private Double fare;
    private Double distanceKm;

    private LocalDateTime requestedAt = LocalDateTime.now();
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

}
