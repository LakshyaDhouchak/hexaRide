package com.lakshya.hexaRide.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.lakshya.hexaRide.dto.LocationUpdateDTO;
import com.lakshya.hexaRide.service.DriverLocationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DriverSocketController {

    // define the properties
    private final DriverLocationService driverLocationService;
    private final SimpMessagingTemplate messagingTemplate;

    // define the methord
    @MessageMapping("/update-location")
    public void updateLocation(LocationUpdateDTO locationDTO){
        driverLocationService.updateLocation(locationDTO.getDriverId(),locationDTO.getLat(),locationDTO.getLng());
        // define the condition
        if(locationDTO.getTripId()!= null){
            messagingTemplate.convertAndSend("/topic/ride/" + locationDTO.getTripId(), locationDTO);
        }
    }
    
}
