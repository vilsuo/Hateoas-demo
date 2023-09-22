
package com.example.demo.employee.unit;

import com.example.demo.employee.Employee;
import com.example.demo.employee.EmployeeCreationObject;
import com.example.demo.employee.EmployeeRepository;
import com.example.demo.employee.EmployeeServiceImpl;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.helpers.AssertionHelper;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import org.mockito.junit.jupiter.MockitoExtension;

/*
@Mock creates a mock. @InjectMocks creates an instance of the class and injects 
the mocks that are created with the @Mock (or @Spy) annotations into this 
instance.

Note you must use @RunWith(MockitoJUnitRunner.class) or Mockito.initMocks(this) 
to initialize these mocks and inject them (JUnit 4).

With JUnit 5, you must use @ExtendWith(MockitoExtension.class).
*/
@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplUnitTest extends EmployeeTestBase {
	
	@Mock
	private EmployeeRepository employeeRepository;
	
	/*
	You can not use @InjectMocks on just the interface alone, because Mockito 
	needs to know what concrete class to instantiate:
	
	Use actual implementation of interface EmployeeService
	*/
	@InjectMocks
	private EmployeeServiceImpl employeeService;
	
	// findAll()
	@Test
	public void findAllEmptyDoesNotFindEmployees() {
		setUpMockedRepositoryFindAll(Arrays.asList());
		
		assertTrue(employeeService.findAll().isEmpty());
	}
	
	// findAll()
	@Test
	public void findAllNonEmptyFindsAllEmployees() {
		setUpMockedRepositoryFindAll(employees);
		
		assertEquals(
			employees.size(),
			employeeService.findAll().size()
		);
		
		for (final Employee employee : employees) {
			assertTrue(employeeService.findAll().contains(employee));
		}
	}
	
	// findById(Long id)
	@Test
	public void findingEmployeeByIdThatExistsFindsTheCorrectEmployee() {
		for (final Employee employee : employees) {
			setUpMockedRepositoryFindById(employee);
			assertEquals(
				employee,
				employeeService.findById(employee.getId())
			);
		}
	}
	
	// findById(Long id)
	@Test
	public void findingEmployeeByIdThatDoesNotExistsThrows() {
		final Long nonExistingId = NON_EXISTING_ID;
		
		setUpMockedRepositoryNotFindindById(nonExistingId);
		
		final Exception exception = assertThrows(
			ResourceNotFoundException.class,
			() -> employeeService.findById(nonExistingId)
		);
		
		AssertionHelper.assertEmployeeNotFoundMessage(
			exception.getMessage(), nonExistingId
		);
	}
	
	// findById(Long id)
	@Test
	public void findingEmployeeByNullIdThrows() {
		final Long nullId = null;
		
		final Exception exception = assertThrows(
			IllegalArgumentException.class,
			() -> employeeService.findById(nullId)
		);
		
		assertTrue(
			exception.getMessage().contains(String.valueOf(nullId))
		);
	}
	
	// create(Employee employee)
	@Test
	public void creatingNewEmployeeReturnsTheCreatedEmployee() {
		final Long nonExistingId = NON_EXISTING_ID;
		final Employee newEmployee = new Employee(
			nonExistingEmployee.getFirstName(),
			nonExistingEmployee.getLastName(),
			nonExistingEmployee.getRole(),
			nonExistingEmployee.getSalary()
		);
		
		final Employee createdEmployee
			= new Employee(
				newEmployee.getFirstName(),
				newEmployee.getLastName(),
				newEmployee.getRole(),
				newEmployee.getSalary()
			);
		createdEmployee.setId(nonExistingId);
		
		setUpMockedRepositorySave(newEmployee, createdEmployee);
		
		assertEquals(
			createdEmployee,
			employeeService.create(nonExistingEmployee)
		);
	}
	
	// create(Employee employee)
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
	
	// updateById(Employee newEmployee, Long id)
	@Test
	public void updatingExistingEmployeeWillUpdateTheEmployeeFieldValues() {
		final Employee orginalEmployee = employee1;
		final Long orginalId = orginalEmployee.getId();
		
		setUpMockedRepositoryFindById(orginalEmployee); // finds
		
		final EmployeeCreationObject newEmployee = nonExistingEmployee;
		
		final Employee updatedEmployee
			= new Employee(
				newEmployee.getFirstName(),
				newEmployee.getLastName(),
				newEmployee.getRole(),
				newEmployee.getSalary()
			);
		updatedEmployee.setId(orginalId);
		
		setUpMockedRepositorySave(updatedEmployee, updatedEmployee);
		
		assertEquals(
			updatedEmployee, 
			employeeService.updateById(newEmployee, orginalId)
		);
	}
	
	// updateById(Employee newEmployee, Long id)
	@Test
	public void updatingNonExistingEmployeeWillCreateNewEmployee() {
		final Long nonExistingId = NON_EXISTING_ID;
		
		setUpMockedRepositoryNotFindindById(nonExistingId); // does not find
		
		final EmployeeCreationObject newEmployee = employeeCreationObject1;
		
		final Employee updatedEmployee
			= new Employee(
				newEmployee.getFirstName(),
				newEmployee.getLastName(),
				newEmployee.getRole(),
				newEmployee.getSalary()
			);
		updatedEmployee.setId(nonExistingId);
		
		setUpMockedRepositorySave(updatedEmployee, updatedEmployee);
		
		assertEquals(
			updatedEmployee, 
			employeeService.updateById(newEmployee, nonExistingId)
		);
	}
	
	// updateById(Employee newEmployee, Long id)
	@Test
	public void updatingNullEmployeeWithExistingIdThrows() {
		final Employee orginalEmployee = employee1;
		final Long orginalId = orginalEmployee.getId();
		
		final EmployeeCreationObject nullEmployee = null;
		
		final Exception exception = assertThrows(
			IllegalArgumentException.class, 
			() -> employeeService.updateById(nullEmployee, orginalId)
		);
		
		assertTrue(
			exception.getMessage().contains(String.valueOf(nullEmployee))
		);
	}
	
	// updateById(Employee newEmployee, Long id)
	@Test
	public void updatingNullEmployeeWithNonExistingIdThrows() {
		final Long nonExistingId = NON_EXISTING_ID;
		
		final EmployeeCreationObject nullEmployee = null;
		
		final Exception exception = assertThrows(
			IllegalArgumentException.class, 
			() -> employeeService.updateById(nullEmployee, nonExistingId)
		);
		
		assertTrue(
			exception.getMessage().contains(String.valueOf(nullEmployee))
		);
	}
	
	// deleteById(Long id)
	@Test
	public void deletingExistingEmployeeByIdDoesNotThrow() {
		for (final Employee employee : employees) {
			setUpMockedRepositoryDelete(employee.getId());
			assertDoesNotThrow(
				() -> employeeService.deleteById(employee.getId())
			);
		}
	}
	
	// deleteById(Long id)
	@Test
	public void deletingNonExistingEmployeeByIdDoesNotThrow() {
		final Long nonExistingId = NON_EXISTING_ID;
		
		setUpMockedRepositoryDelete(nonExistingId);
		
		assertDoesNotThrow(
			() -> employeeService.deleteById(nonExistingId)
		);
	}
	
	// deleteById(Long id)
	@Test
	public void deletingEmployeeByNullIdThrows() {
		final Long nonExistingId = null;
		
		final IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> employeeService.deleteById(nonExistingId)
		);
		
		assertTrue(
			exception.getMessage().contains(String.valueOf(nonExistingId))
		);
	}
	
	// #########################################################################
	// helpers
	
	private void setUpMockedRepositoryFindAll(
			final List<Employee> employees) {
		
		given(employeeRepository.findAll())
			.willReturn(employees);
	}
	
	private void setUpMockedRepositoryFindById(final Employee employee) {
		given(employeeRepository.findById(employee.getId()))
			.willReturn(Optional.of(employee));
	}
	
	private void setUpMockedRepositoryNotFindindById(final Long id) {
		given(employeeRepository.findById(id))
			.willReturn(Optional.empty());
	}
	
	private void setUpMockedRepositorySave(
			final Employee employeeToBeSaved, 
			final Employee employeeToBeReturned) {
		
		given(employeeRepository.save(employeeToBeSaved))
			.willReturn(employeeToBeReturned);
	}
	
	private void setUpMockedRepositoryDelete(final Long id) {
		doNothing()
			.when(employeeRepository)
			.deleteById(id);
	}
	
	private void setUpMockedRepositoryDeleteThrowing(final Long id) {
		doThrow(IllegalArgumentException.class)
			.when(employeeRepository).deleteById(id);
	}
}
