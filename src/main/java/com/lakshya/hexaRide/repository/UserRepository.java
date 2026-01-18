package com.lakshya.hexaRide.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lakshya.hexaRide.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    // define the properties
    Optional<User> existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
