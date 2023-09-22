
package com.example.demo.employee.unit;

import com.example.demo.employee.Employee;
import com.example.demo.employee.EmployeeCreationObject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;

public class EmployeeTestBase {
	
	protected static final Long EXISTING_ID_1 = 1l;
	protected static final Long EXISTING_ID_2 = 2l;
	
	protected static final Long NON_EXISTING_ID
		= 2 * (EXISTING_ID_1 + EXISTING_ID_2);
	
	protected Employee employee1;
	protected Employee employee2;
	protected EmployeeCreationObject employeeCreationObject1;
	protected EmployeeCreationObject employeeCreationObject2;
	
	protected List<Employee> employees;
	protected List<EmployeeCreationObject> employeeCreationObjects;
	
	protected EmployeeCreationObject nonExistingEmployee;
	
	@BeforeAll
	private static void assertIdsAreUnique() {
		assertNotEquals(EXISTING_ID_1, EXISTING_ID_2);
		assertNotEquals(EXISTING_ID_1, NON_EXISTING_ID);
		assertNotEquals(EXISTING_ID_2, NON_EXISTING_ID);
	}
	
	@BeforeEach
	private void setUpEmployees() {
		employee1 = new Employee("Bilbo", "Baggins", "burglar", 1d);
		employee2 = new Employee("Frodo", "Baggins", "thief", 10d);
		employeeCreationObject1 = new EmployeeCreationObject("Bilbo", "Baggins", "burglar", 1d);
		employeeCreationObject2 = new EmployeeCreationObject("Frodo", "Baggins", "thief", 10d);
		
		employee1.setId(EXISTING_ID_1);
		employee2.setId(EXISTING_ID_2);
		
		employees = Arrays.asList(employee1, employee2);
		employeeCreationObjects = Arrays.asList(employeeCreationObject1, employeeCreationObject2);
		
		nonExistingEmployee = new EmployeeCreationObject("With", "Nullid", "empty", 1000d);
	}
	
	protected static Stream<Arguments> invalidEmployeesMethodSource() {
		final String nullString = null;
		final String emptyString = "";
		final String wsString = " ";
		final Double invalidSalary = -1d;
		
		final String validFirstName = "First";
		final String validLastName = "Last";
		final String validRole = "Important";
		
		final Double validSalary = 0d;
		
		return Stream.of(
			// invalid first names
			Arguments.of(new Employee(nullString,	validLastName, validRole, validSalary)),
			Arguments.of(new Employee(emptyString,	validLastName, validRole, validSalary)),
			Arguments.of(new Employee(wsString,		validLastName, validRole, validSalary)),
		
			// invalid last names
			Arguments.of(new Employee(validFirstName, nullString,	validRole, validSalary)),
			Arguments.of(new Employee(validFirstName, emptyString,	validRole, validSalary)),
			Arguments.of(new Employee(validFirstName, wsString,		validRole, validSalary)),
					
			// invalid roles names
			Arguments.of(new Employee(validFirstName, validLastName, nullString,	validSalary)),
			Arguments.of(new Employee(validFirstName, validLastName, emptyString,	validSalary)),
			Arguments.of(new Employee(validFirstName, validLastName, wsString,		validSalary)),
				
			// invalid salaries
			Arguments.of(new Employee(validFirstName, validLastName, validRole,	invalidSalary)),
			Arguments.of(new Employee(validFirstName, validLastName, validRole,	invalidSalary)),
			Arguments.of(new Employee(validFirstName, validLastName, validRole,	invalidSalary))
		);
	}
}
