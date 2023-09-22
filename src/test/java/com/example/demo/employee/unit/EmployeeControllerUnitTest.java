
package com.example.demo.employee.unit;

import com.example.demo.employee.Employee;
import com.example.demo.employee.EmployeeController;
import com.example.demo.employee.EmployeeCreationObject;
import com.example.demo.employee.EmployeeModelAssembler;
import com.example.demo.employee.EmployeeService;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.helpers.JsonHelper;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/*
in this test, Spring Boot instantiates only the web layer rather than the whole 
context. In an application with multiple controllers, you can even ask for only 
one to be instantiated

@WebMvcTest auto-configures the Spring MVC infrastructure and limits scanned 
beans to @Controller, @ControllerAdvice, @JsonComponent, Converter, 
GenericConverter, Filter, WebMvcConfigurer, and HandlerMethodArgumentResolver
*/
@Import({ EmployeeModelAssembler.class })
@WebMvcTest(EmployeeController.class)
public class EmployeeControllerUnitTest extends EmployeeTestBase {
	
	@Autowired
	private MockMvc mockMvc;

	// We use @MockBean to create and inject a mock
	@MockBean
	private EmployeeService employeeService;
	
	//@MockBean
	//private EmployeeModelAssembler employeeModelAssembler;
	//
	// mocked but identical to a real one...
	//private void setUpMockedAssemblerToModel(final Employee employee) {
	//	final EntityModel<Employee> m1 = EntityModel.of(
	//		employee,
	//		linkTo(methodOn(EmployeeController.class).one(employee.getId()))
	//			.withSelfRel(),
	//		
	//		linkTo(methodOn(EmployeeController.class).all())
	//			.withRel("employees")
	//	);
	//	given(employeeModelAssembler.toModel(employee))
	//		.willReturn(m1);
	//}
	
	// @GetMapping("/employees") all
	@Test
	public void findingEmptyEmployeesReturnsNoEmployees() 
			throws Exception {
		
		final List<Employee> emptyEmployees = Arrays.asList();
		
		setUpMockedServiceFindAll(emptyEmployees);
		
		mockMvc.perform(
				get("/employees")
					.contentType(MediaType.APPLICATION_JSON)
			).andExpect(status().isOk())
			.andExpect(content().contentType(MediaTypes.HAL_JSON))
				
			// contains self link
			// does not contain any Employees
			.andExpectAll(getEmployeesListResultMatcherArray(emptyEmployees));
	}
	
	// @GetMapping("/employees") all
	@Test
	public void findingNonEmptyEmployeesReturnsAllEmployees()
			throws Exception {

		setUpMockedServiceFindAll(employees);
		
		//for (final Employee employee : employees) {
		//	setUpMockedAssemblerToModel(employee);
		//}
		
		mockMvc.perform(
				get("/employees")
					.contentType(MediaType.APPLICATION_JSON)
			).andExpect(status().isOk())
			.andExpect(content().contentType(MediaTypes.HAL_JSON))
				
			// contains self link
			// embedded Employee list fits all Employees
			.andExpectAll(getEmployeesListResultMatcherArray(employees))
				
			// contains all Employees
			.andExpectAll(getEmployeeResultMatcherArray(employee1, 0))
			.andExpectAll(getEmployeeResultMatcherArray(employee2, 1));
	}
	
	// @GetMapping("/employees/{id}") one
	@Test
	public void findingEmployeeByIdThatExistsFindsTheCorrectOne() 
			throws Exception {
		
		//for (final Employee employee : employees) {
		//	setUpMockedAssemblerToModel(employee);
		//}
		
		for (final Employee employee : employees) {
			setUpMockedServiceFindById(employee.getId(), employee);
			mockMvc.perform(
					get("/employees/{id}", employee.getId(), Long.class)
						.contentType(MediaType.APPLICATION_JSON)
				).andExpect(status().isOk())
				.andExpect(content().contentType(MediaTypes.HAL_JSON))
				.andExpectAll(getEmployeeResultMatcherArray(employee, null));
		}
	}
	
	// @GetMapping("/employees/{id}") one
	@Test
	public void findingEmployeeByIdThatDoesNotExistsDoesNotFindAny() 
			throws Exception {
		
		final Long nonExistingId = NON_EXISTING_ID;
		
		setUpMockedServiceFindByIdThrowing(
			nonExistingId, ResourceNotFoundException.class
		);
		
		mockMvc.perform(
				get("/employees/{id}", nonExistingId, Long.class)
					.contentType(MediaType.APPLICATION_JSON)
			).andExpect(status().isNotFound());
	}
	
	// @PostMapping("/employees") newEmployee
	@Test
	public void creatingEmployeeWithNonExistingIdReturnsCreated() 
			throws Exception {
		
		final Long nonExistingId = NON_EXISTING_ID;
		final EmployeeCreationObject newEmployee = nonExistingEmployee;
		
		final Employee createdEmployee
			= new Employee(
				newEmployee.getFirstName(),
				newEmployee.getLastName(),
				newEmployee.getRole(),
				newEmployee.getSalary()
			);
		createdEmployee.setId(nonExistingId);
		
		setUpMockedServiceCreate(newEmployee, createdEmployee);
		
		//setUpMockedAssemblerToModel(createdEmployee);
		
		mockMvc.perform(
				post("/employees")
					.content(JsonHelper.asJsonString(newEmployee))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
			).andExpect(status().isCreated())
			.andExpectAll(getEmployeeResultMatcherArray(createdEmployee, null));
	}
	
	// @PostMapping("/employees") newEmployee
	@Test
	public void creatingEmployeeWithExistingIdReturnsCreated() 
			throws Exception {
		
		final Employee existingEmployee = employee1;
		final EmployeeCreationObject newEmployee = employeeCreationObject1;
		
		setUpMockedServiceCreate(newEmployee, existingEmployee);
		
		//setUpMockedAssemblerToModel(existingEmployee);
		
		mockMvc.perform(
				post("/employees")
					.content(JsonHelper.asJsonString(newEmployee))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
			).andExpect(status().isCreated())
			.andExpectAll(
				getEmployeeResultMatcherArray(existingEmployee, null)
			);
	}
	
	// @PutMapping("/employees/{id}") replaceEmployee
	@Test
	public void updatingExistingEmployeeReturnsCreated() throws Exception {
		final Employee orginalEmployee = employee1;
		final Long orginalId = orginalEmployee.getId();
		
		final EmployeeCreationObject newEmployee = nonExistingEmployee;
		
		final Employee updatedEmployee
			= new Employee(
				newEmployee.getFirstName(),
				newEmployee.getLastName(),
				newEmployee.getRole(),
				newEmployee.getSalary()
			);
		updatedEmployee.setId(orginalId);
		
		setUpMockedServiceUpdateById(newEmployee, orginalId, updatedEmployee);
		
		//setUpMockedAssemblerToModel(updatedEmployee);
		
		mockMvc.perform(
				put("/employees/{id}", orginalId, Long.class)
					.content(JsonHelper.asJsonString(newEmployee))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
			).andExpect(status().isCreated())
			.andExpectAll(getEmployeeResultMatcherArray(updatedEmployee, null));
	}
	
	// @PutMapping("/employees/{id}") replaceEmployee
	@Test
	public void updatingNonExistingEmployeeReturnsCreated() throws Exception {
		final Long nonExistingId = NON_EXISTING_ID;
		
		final EmployeeCreationObject newEmployee = nonExistingEmployee;
		
		final Employee updatedEmployee = new Employee(
			newEmployee.getFirstName(),
			newEmployee.getLastName(),
			newEmployee.getRole(),
			newEmployee.getSalary()
		);
		updatedEmployee.setId(nonExistingId);
		
		setUpMockedServiceUpdateById(
			newEmployee, nonExistingId, updatedEmployee
		);
		
		//setUpMockedAssemblerToModel(updatedEmployee);
		
		mockMvc.perform(
				put("/employees/{id}", nonExistingId, Long.class)
					.content(JsonHelper.asJsonString(newEmployee))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
			).andExpect(status().isCreated())
			.andExpectAll(getEmployeeResultMatcherArray(updatedEmployee, null));
	}
	
	// @DeleteMapping("/employees/{id}") deleteEmployee
	@Test
	public void deletingEmployeeByExistingIdReturnsNoContent() 
			throws Exception {
		
		for (final Employee employee : employees) {
			setUpMockedServiceDeleteById(employee.getId());
			
			mockMvc.perform(
				delete("/employees/{id}", employee.getId(), Long.class)
			).andExpect(status().isNoContent());
		}
	}
	
	// @DeleteMapping("/employees/{id}") deleteEmployee
	@Test
	public void deletingEmployeeByNonExistingIdReturnsNoContent() 
			throws Exception {
		
		final Long nonExistingId = NON_EXISTING_ID;
		
		setUpMockedServiceDeleteById(nonExistingId);
		
		mockMvc.perform(
				delete("/employees/{id}", nonExistingId, Long.class)
			).andExpect(status().isNoContent());
	}
	
	// #########################################################################
	// helpers
	
	private void setUpMockedServiceFindAll(final List<Employee> employees) {
		given(employeeService.findAll())
			.willReturn(employees);
	}
	
	private void setUpMockedServiceFindById(
			final Long id, final Employee employee) {
		
		given(employeeService.findById(id))
			.willReturn(employee);
	}
	
	// can not throw
	// IllegalArgumentException
	//		id == null
	
	// can throw
	// EmployeeNotFoundException
	//		employee not found with given id
	private void setUpMockedServiceFindByIdThrowing(
			final Long id, final Class<? extends Exception> ex) {
		
		given(employeeService.findById(id))
			.willThrow(ex);
	}
	
	private void setUpMockedServiceCreate(
			final EmployeeCreationObject employeeToBeCreated, 
			final Employee employeeToBeReturned) {
		
		given(employeeService.create(employeeToBeCreated))
			.willReturn(employeeToBeReturned);
	}
	
	// can not throw
	// IllegalArgumentException
	//		employeeToBeCreated == null
	private void setUpMockedServiceCreateThrowing(
			final EmployeeCreationObject employeeToBeCreated, 
			final Class<? extends Exception> ex) {
		
		given(employeeService.create(employeeToBeCreated))
			.willThrow(ex);
	}
	
	private void setUpMockedServiceUpdateById(
			final EmployeeCreationObject newEmployee, final Long id,
			final Employee employeeToBeReturned) {
		
		given(employeeService.updateById(newEmployee, id))
			.willReturn(employeeToBeReturned);
	}
	
	// can not throw
	// IllegalArgumentException 
	//		id == null
	//		newEmployee == null
	private void setUpMockedServiceUpdateByIdThrowing(
			final EmployeeCreationObject newEmployee, final Long id,
			final Class<? extends Exception> ex) {
		
		given(employeeService.updateById(newEmployee, id))
			.willThrow(ex);
	}
	
	private void setUpMockedServiceDeleteById(final Long id) {
		doNothing()
			.when(employeeService)
			.deleteById(id);
	}
	
	// can not throw
	// IllegalArgumentException
	//		id == null
	private void setUpMockedServiceDeleteByIdThrowing(
			final Long id, final Class<? extends Exception> ex) {
		
		doThrow(ex)
			.when(employeeService)
			.deleteById(id);
	}
	
	////////////////////////////////////////////////////////////////////////
	//								JSON helpers						  //
	////////////////////////////////////////////////////////////////////////
	
	private final String SCHEME_AND_AUTHORITY = "http://localhost";
	private final String JSON_RELATIVE_SELF_LINK_PATH = "._links.self.href";
	private final String JSON_EMPLOYEE_RELATIVE_EMPLOYEES_LINK_PATH = "._links.employees.href";
	private final String JSON_EMBEDDED_EMPLOYEE_LIST_PATH = "$._embedded.employeeList";
	
	private ResultMatcher[] getEmployeesListResultMatcherArray(
			final List<Employee> employees) {
		
		final ResultMatcher sizeMatcher = employees.isEmpty()
			? jsonPath(JSON_EMBEDDED_EMPLOYEE_LIST_PATH).doesNotExist()
			: jsonPath(JSON_EMBEDDED_EMPLOYEE_LIST_PATH, hasSize(employees.size()));
		
		return new ResultMatcher[] {
			sizeMatcher,
			
			jsonPath(
				"$" + JSON_RELATIVE_SELF_LINK_PATH,
				is(SCHEME_AND_AUTHORITY + "/employees")
			)
		};
	}
	
	private ResultMatcher[] getEmployeeResultMatcherArray(
			final Employee employee, final Integer index) {
		
		final String prefix = (index != null)
			? (JSON_EMBEDDED_EMPLOYEE_LIST_PATH + "[" + index + "]")
			: "$";
		
		return new ResultMatcher[] {
			// field values
			jsonPath(prefix + ".id", is(employee.getId()), Long.class),
			jsonPath(prefix + ".firstName", is(employee.getFirstName())),
			jsonPath(prefix + ".lastName", is(employee.getLastName())),
			jsonPath(prefix + ".role", is(employee.getRole())),
			jsonPath(prefix + ".salary", is(employee.getSalary()), Double.class),
			
			// links
			jsonPath(
				prefix + JSON_RELATIVE_SELF_LINK_PATH,
				is(SCHEME_AND_AUTHORITY + "/employees/" + employee.getId())
			),
			jsonPath(
				prefix + JSON_EMPLOYEE_RELATIVE_EMPLOYEES_LINK_PATH, 
				is(SCHEME_AND_AUTHORITY + "/employees")
			)
		};
	}
}
