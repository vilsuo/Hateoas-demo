
package com.example.demo.employee.integration;

import com.example.demo.employee.Employee;
import com.example.demo.employee.EmployeeCreationObject;
import com.example.demo.employee.EmployeeService;
import com.example.demo.employee.unit.EmployeeTestBase;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.helpers.AssertionHelper;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Transactional
@SpringBootTest
public class EmployeeServiceImplIntegrationTest extends EmployeeTestBase {
	
	@Autowired
	private EmployeeService employeeService;
	
	@Test
	public void findsEmptyEmployeeListInEmptyDatabase() {
		assertTrue(
			employeeService.findAll().isEmpty()
		);
	}
	
	@Test
	public void createdEmployeeHasNonNullId() {
		for (final EmployeeCreationObject obj : employeeCreationObjects) {
			assertTrue(
				employeeService.create(obj).getId() != null
			);
		}
	}
	
	@Test
	public void createdEmployeeHasValuesFromParameter() {
		for (final EmployeeCreationObject obj : employeeCreationObjects) {
			final Employee createdEmployee = employeeService.create(obj);

			assertEmployeeIsCreatedFromEmployeeCreationObject(
				obj, createdEmployee
			);
		}
	}
	
	@Test
	public void createdEmployeesCanBeFound() {
		int createdInTotal = 0;
		for (final EmployeeCreationObject obj : employeeCreationObjects ) {
			final Employee createdEmployee = employeeService.create(obj);
			++createdInTotal;
			
			assertEquals(
				createdInTotal,
				employeeService.findAll().size()
			);
			
			assertTrue(
				employeeService.findAll().contains(createdEmployee)
			);
			
			assertEquals(
				createdEmployee,
				employeeService.findById(createdEmployee.getId())
			);
		}
	}
	
	@Test
	public void creatingNullEmployeeThrows() {
		final EmployeeCreationObject nullEmployee = null;
		final IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> employeeService.create(nullEmployee)
		);
		
		assertTrue(
			exception.getMessage().contains(String.valueOf(nullEmployee))
		);
	}
	
	@Test
	public void findingByNullIdThrows() {
		final Long nullId = null;
		final IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> employeeService.findById(nullId)
		);
		
		assertTrue(
			exception.getMessage().contains(String.valueOf(nullId))
		);
	}
	
	@Test
	public void findingByIdThatDoesNotExistThrows() {
		final Long nonExistingId = 1l;
		final ResourceNotFoundException exception = assertThrows(
			ResourceNotFoundException.class,
			() -> employeeService.findById(nonExistingId)
		);
		
		AssertionHelper.assertEmployeeNotFoundMessage(
			exception.getMessage(), nonExistingId
		);
	}

	@Test
	public void updatingWithExistingIdUpdatesExistingEmployee() {
		final Employee employeeToBeUpdated = employeeService
			.create(employeeCreationObject1);
		
		final EmployeeCreationObject newEmployee = employeeCreationObject2;
		
		final Employee updatedEmployee = employeeService
			.updateById(newEmployee, employeeToBeUpdated.getId());
		
		assertEquals(
			employeeService.findById(employeeToBeUpdated.getId()),
			updatedEmployee
		);
		
		assertEmployeeIsCreatedFromEmployeeCreationObject(
			newEmployee, updatedEmployee
		);
	}
	
	@Test
	public void updatingWithNonExistingIdCreatesNewEmployee() {
		final Long nonExistingId = NON_EXISTING_ID;
		final EmployeeCreationObject newEmployee = employeeCreationObject1;
		
		final Employee createdEmployee = employeeService
			.updateById(newEmployee, nonExistingId);
		
		assertEmployeeIsCreatedFromEmployeeCreationObject(
			newEmployee, createdEmployee
		);
	}
	
	@Test
	public void updatingByNullIdThrows() {
		final Long nullId = null;
		final IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> employeeService.deleteById(nullId)
		);
		
		assertTrue(
			exception.getMessage().contains(String.valueOf(nullId))
		);
	}
	
	// test with existing id and not existing id
	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	public void updatingByNullEmployeThrows(final boolean useExistingId) {
		final Long id = useExistingId
			? employeeService.create(employeeCreationObject1).getId()
			: NON_EXISTING_ID;
		
		final EmployeeCreationObject nullEmployee = null;
		final IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> employeeService.updateById(nullEmployee, id)
		);
		
		assertTrue(
			exception.getMessage().contains(String.valueOf(nullEmployee))
		);
	}
	
	
	@Test
	public void deletedEmployeesCanNotBeFound() {
		final List<Employee> createdEmployees = new ArrayList<>();
		for (final EmployeeCreationObject obj : employeeCreationObjects ) {
			createdEmployees.add(employeeService.create(obj));
		}
		
		int deletedInTotal = 0;
		for (final Employee employee : createdEmployees) {
			employeeService.deleteById(employee.getId());
			++deletedInTotal;
		
			assertEquals(
				createdEmployees.size() - deletedInTotal,
				employeeService.findAll().size()
			);
			
			assertFalse(
				employeeService.findAll().contains(employee)
			);
		}
	}
	
	@Test
	public void deletingByNullIdThrows() {
		final Long nullId = null;
		final IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> employeeService.deleteById(nullId)
		);
		
		assertTrue(
			exception.getMessage().contains(String.valueOf(nullId))
		);
	}
	
	private void assertEmployeeIsCreatedFromEmployeeCreationObject(
			final EmployeeCreationObject obj, final Employee created) {
		
		assertEquals(
			obj.getFirstName(), created.getFirstName()
		);
		
		assertEquals(
			obj.getLastName(), created.getLastName()
		);
		
		assertEquals(
			obj.getRole(), created.getRole()
		);
		
		assertEquals(
			obj.getSalary(), created.getSalary()
		);
	}
}
