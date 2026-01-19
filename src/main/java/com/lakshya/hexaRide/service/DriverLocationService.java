package com.lakshya.hexaRide.service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.uber.h3core.H3Core;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DriverLocationService  {
    // define the properties
    private final RedisTemplate<String,String> redisTemplate;
    private H3Core h3;
    @PostConstruct
    public void init() {
        try {
            this.h3 = H3Core.newInstance();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize H3Core", e);
        }
    }
    private static final int H3_RESOLUTION = 7;
    private static final String Hex_PREFIX = "HEXAGON:";
    private static final String DRIVER_LAST_HEX = "DRIVER_LAST_HEX:";

    // define the methord
    public void updateLocation(Long Driver_Id , double lat , double lng){
        String driverIdStr = Driver_Id.toString();
        String newHex = h3.latLngToCellAddress(lat,lng,H3_RESOLUTION);
        String oldHex = redisTemplate.opsForValue().get(DRIVER_LAST_HEX+driverIdStr);

        // define the condition
        if(oldHex != null && !oldHex.equals(newHex)){
            redisTemplate.opsForSet().remove(Hex_PREFIX+oldHex, driverIdStr);
        }
        redisTemplate.opsForSet().add(Hex_PREFIX+newHex,driverIdStr);
        redisTemplate.opsForValue().set(DRIVER_LAST_HEX + driverIdStr, newHex, 60, TimeUnit.SECONDS);
        redisTemplate.expire(Hex_PREFIX + newHex, 60, TimeUnit.SECONDS);
    }

}
