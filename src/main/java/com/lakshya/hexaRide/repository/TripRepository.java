package com.lakshya.hexaRide.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lakshya.hexaRide.entity.Trip;
import com.lakshya.hexaRide.entity.User;
import com.lakshya.hexaRide.enums.TripStatus;

@Repository
public interface TripRepository extends JpaRepository<Trip,Long> {
    // define the methord
    boolean existsByRiderAndStatusIn(User rider, List<TripStatus> statuses);
    boolean existsByDriverAndStatusIn(User driver, List<TripStatus> statuses);
}
