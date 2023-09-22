
package com.example.demo.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

/*
are these needed?
- illegal argument handler? (null values)
-  MethodArgumentNotValidException (validating requestbody in parametrized tests)
*/
@ControllerAdvice
public class ExceptionHandlingControllerAdvice {

	/*
	When an EmployeeNotFoundException is thrown, this extra tidbit of Spring MVC
	configuration is used to render an HTTP 404:
	
	@ResponseBody signals that this advice is rendered straight into the 
	response body.

	@ExceptionHandler configures the advice to only respond if an 
	EmployeeNotFoundException is thrown.

	@ResponseStatus says to issue an HttpStatus.NOT_FOUND, i.e. an HTTP 404.

	The body of the advice generates the content. In this case, it gives the 
	message of the exception.
	*/
	
	/*
	@ResponseBody
	@ExceptionHandler(EmployeeNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String employeeNotFoundHandler(EmployeeNotFoundException ex) {
		System.out.println("in employeeNotFoundHandler");
		return ex.getMessage();
	}
	
	@ResponseBody
	@ExceptionHandler(OrderNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String orderNotFoundHandler(OrderNotFoundException ex) {
		System.out.println("in orderNotFoundHandler");
		return ex.getMessage();
	}
	*/
	
	@ResponseBody
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String illegalArgumentHandler(IllegalArgumentException ex) {
		System.out.println("in illegalArgumentHandler");
		return ex.getMessage();
	}
	
	@ResponseBody
	@ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
			ResourceNotFoundException exception, WebRequest webRequest) {
		
        ErrorDetails error
			= new ErrorDetails(
				LocalDateTime.now(), 
				exception.getMessage(),
                webRequest.getDescription(false)
			);
		
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
