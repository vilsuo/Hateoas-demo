
package com.example.demo.exception;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorDetails {
	
    final private LocalDateTime timestamp;
    final private String message;
    final private String details;
	
}