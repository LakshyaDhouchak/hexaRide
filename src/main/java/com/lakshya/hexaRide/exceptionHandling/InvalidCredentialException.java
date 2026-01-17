package com.lakshya.hexaRide.exceptionHandling;

public class InvalidCredentialException  extends HexaRideException{
    // define the constructor
    public InvalidCredentialException(String message){
        super(message);
    }
}
