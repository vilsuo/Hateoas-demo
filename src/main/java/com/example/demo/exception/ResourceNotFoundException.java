
package com.example.demo.exception;

import lombok.Getter;

/*
@ResponseStatus Warning: when using this annotation on an exception class, or 
when setting the reason attribute of this annotation, the 
HttpServletResponse.sendError method will be used.

With HttpServletResponse.sendError, the response is considered complete and 
should not be written to any further. Furthermore, the Servlet container will 
typically write an HTML error page therefore making the use of a reason 
unsuitable for REST APIs. For such cases it is preferable to use a 
ResponseEntity as a return type and avoid the use of @ResponseStatus altogether.
*/
@Getter
public class ResourceNotFoundException extends RuntimeException {
	
    private final String resourceName;
    private final String fieldName;
    private final String fieldValue;

    public ResourceNotFoundException(final String resourceName,
			final String fieldName, final String fieldValue) {
		
        super(
			resourceName + " not found with "
			+ fieldName + " : '" + fieldValue + "'"
		);
        
		this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}