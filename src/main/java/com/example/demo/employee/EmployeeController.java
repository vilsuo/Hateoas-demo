
package com.example.demo.employee;

import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/*
TODO
- add links to replace/delete employee

- are links ok?
	- do not handle any links in this class? create elsewere?

- return model or response entity?
*/

/*
Right Status Codes for REST APIs

Returning the right HTTP status codes in REST APIs is also important, to ensure 
uniform interface architectural constraint. Besides the status codes above, 
below is the guideline for common HTTP status codes:

200 OK: for successful requests
201 Created: for successful creation requests
202 Accepted: the server accepts the request, but the response cannot be sent 
	immediately (e.g. in batch processing)
204 No Content: for successful operations that contain no data
304 Not Modified: used for caching, indicating the resource is not modified
400 Bad Request: for failed operation when input parameters are incorrect or 
	missing, or the request itself is incomplete
401 Unauthorized: for failed operation due to unauthenticated requests
403 Forbidden: for failed operation when the client is not authorized to perform
404 Not Found: for failed operation when the resource doesn’t exist
405 Method Not Allowed: for failed operation when the HTTP method is not allowed
	for the requested resource
406 Not Acceptable: for failed operation when the Accept header doesn’t match. 
	Also can be used to refuse request
409 Conflict: for failed operation when an attempt is made for a duplicate 
	create operation
429 Too Many Requests: for failed operation when a user sends too many requests 
	in a given amount of time (rate limiting)
500 Internal Server Error: for failed operation due to server error (generic)
502 Bad Gateway: for failed operation when the upstream server calls fail (e.g. 
	call to a third-party service fails)
503 Service Unavailable: for a failed operation when something unexpected 
	happened at the server (e.g. overload of service fails)
*/


/*
https://stackoverflow.com/questions/72187414/pathvariable-validation-gives-500-instead-of-400

Validating a Request Body:

We can put the @Valid annotation on method parameters and fields to tell Spring 
that we want a method parameter or field to be validated.

In POST and PUT requests, it’s common to pass a JSON payload within the request 
body. Spring automatically maps the incoming JSON to a Java object. Now, we want
to check if the incoming Java object meets our requirements.

To validate the request body of an incoming HTTP request, we annotate the 
request body with the @Valid annotation in a REST controller

If the validation fails, it will trigger a MethodArgumentNotValidException. By 
default, Spring will translate this exception to a HTTP status 400 (Bad Request)


Validating Path Variables and Request Parameters:

Validating path variables and request parameters works a little differently.
We’re not validating complex Java objects in this case, since path variables and
request parameters are primitive types like int or their counterpart objects 
like Integer or String. Instead of annotating a class field like above, we’re 
adding a constraint annotation  directly to the method parameter in the Spring 
controller

Note that we have to add Spring’s @Validated annotation to the controller at 
class level to tell Spring to evaluate the constraint annotations on method 
parameters.

The @Validated annotation is only evaluated on class level in this case, even 
though it’s allowed to be used on methods.

In contrast to request body validation a failed validation will trigger a 
ConstraintViolationException instead of a MethodArgumentNotValidException. 
Spring does not register a default exception handler for this exception, so it 
will by default cause a response with HTTP status 500 (Internal Server Error).

If we want to return a HTTP status 400 instead (which makes sense, since the 
client provided an invalid parameter, making it a bad request), we can add a 
custom exception handler to our contoller
*/
@RestController
public class EmployeeController {
	
	private final EmployeeService employeeService;
	
	private final EmployeeModelAssembler assembler;

	public EmployeeController(
			EmployeeService employeeService, EmployeeModelAssembler assembler) {
		
		this.employeeService = employeeService;
		this.assembler = assembler;
	}
	
	@GetMapping("/employees")
	public CollectionModel<EntityModel<Employee>> all() {
		/*
		CollectionModel<> is Spring HATEOAS container; it’s aimed at 
		encapsulating collections of resources

		Since we’re talking REST, it should encapsulate collections of employee 
		resources
		*/
		return assembler.toCollectionModel(employeeService.findAll());
	}
	
	@GetMapping("/employees/{id}")
	public EntityModel<Employee> one(@PathVariable final Long id) {
		/*
		EntityModel<T> is a generic container from Spring HATEOAS that includes 
		not only the data but a collection of links
		
		A critical ingredient to any RESTful service is adding links to relevant
		operations. To make your controller more RESTful, add links like this:
		*/
		return assembler.toModel(employeeService.findById(id));
		/*
		What do we mean by "build a link"? One of Spring HATEOAS’s core types is
		Link. It includes a URI and a rel (relation)
		*/
	}

	// if newEmployee is invalid: throws MethodArgumentNotValidException
	@PostMapping("/employees")
	public ResponseEntity<?> newEmployee(
			@Valid @RequestBody final EmployeeCreationObject newEmployee) {
		
		final EntityModel<Employee> entityModel = assembler
			.toModel(employeeService.create(newEmployee));
		
		/*
		Spring MVC’s ResponseEntity is used to create an HTTP 201 Created status 
		message. This type of response typically includes a Location response 
		header, and we use the URI derived from the model’s self-related link.

		Additionally, return the model-based version of the saved object.
		*/
		return ResponseEntity
			.created(
				entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()
			).body(entityModel);
	}

	// if newEmployee is invalid: throws MethodArgumentNotValidException
	@PutMapping("/employees/{id}")
	public ResponseEntity<?> replaceEmployee(
			@Valid @RequestBody final EmployeeCreationObject newEmployee, 
			@PathVariable final Long id) {
		
		final Employee updatedEmployee = employeeService
			.updateById(newEmployee, id);
		
		final EntityModel<Employee> entityModel = assembler
			.toModel(updatedEmployee);

		return ResponseEntity
			/*
			Since we want a more detailed HTTP response code than 200 OK, we 
			will use Spring MVC’s ResponseEntity wrapper. It has a handy static 
			method created() where we can plug in the resource’s URI. It’s 
			debatable if HTTP 201 Created carries the right semantics since we 
			aren’t necessarily "creating" a new resource
			*/
			.created(
				entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()
			).body(entityModel);
	}

	@DeleteMapping("/employees/{id}")
	public ResponseEntity<?> deleteEmployee(@PathVariable final Long id) {
		employeeService.deleteById(id);

		return ResponseEntity.noContent().build();
	}
	
	/*
	For partial update operation, the HTTP request method should be PATCH and 
	the response status code is 200 OK for successful update operation
	*/
	//@PatchMapping("/employees/{id}/raise")
	//public ResponseEntity<?> raise(@PathVariable final Long id, 
	//		@RequestParam final Double amount) {
	//	
	//	final Employee updatedEmployee = employeeService.raise(id, amount);
	//
	//	return ResponseEntity.ok(assembler.toModel(updatedEmployee));
	//}
}
