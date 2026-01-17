package com.lakshya.hexaRide.exceptionHandling;

public class ResourceNotFoundException extends HexaRideException {
    // define the constructor
    public ResourceNotFoundException(String message){
        super(message);
    }
}
