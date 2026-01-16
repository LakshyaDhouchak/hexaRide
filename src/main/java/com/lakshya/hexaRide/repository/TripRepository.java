package com.lakshya.hexaRide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lakshya.hexaRide.entity.Trip;

@Repository
public interface TripRepository extends JpaRepository<Trip,Long> {

}
