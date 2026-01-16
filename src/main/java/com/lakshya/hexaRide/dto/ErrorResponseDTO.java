package com.lakshya.hexaRide.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO {
    
    private LocalDateTime timestamp;
    private int status;        
    private String error;      
    private String message;     
    private String path;        
    
}
