package com.lakshya.hexaRide.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lakshya.hexaRide.entity.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle ,Long> {
    Optional<Vehicle> findByDriverId(Long id);
    
}
