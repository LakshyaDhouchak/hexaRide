package com.lakshya.hexaRide.entity;

import com.lakshya.hexaRide.enums.VehicleType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehicles")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Vehicle {
    // define vehicle attribute here
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false, unique = true)
    private User driver;

    @Column(name = "vehicle_number", unique = true, nullable = false)
    private String vehicleNumber;

    @Column(nullable = false)
    private String model;

    private String color;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;
}