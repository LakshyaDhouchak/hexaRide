package com.lakshya.hexaRide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lakshya.hexaRide.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
 
}
