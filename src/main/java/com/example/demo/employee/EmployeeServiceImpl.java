
package com.example.demo.employee;

import com.example.demo.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

/*
no validating here: validate in controller layer instead
*/
@Service
public class EmployeeServiceImpl implements EmployeeService {
	
	private final EmployeeRepository employeeRepository;
	
	/*
	It is recommend using constructor injection to wire up dependencies
	
	Notice how using constructor injection lets the employeeRepository field be 
	marked as final
	
	If a bean has more than one constructor, you will need to mark the one you 
	want Spring to use with @Autowired
	*/
	public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}
	
	@Override
	public List<Employee> findAll() {
		return employeeRepository.findAll();
	}
	
	@Override
	public Employee findById(final Long id) 
			throws IllegalArgumentException, ResourceNotFoundException {
		
		if (id == null) {
			throw new IllegalArgumentException("Id can not be null");
		}
		
		return employeeRepository.findById(id)
			.orElseThrow(
				() -> new ResourceNotFoundException(
					"Employee", "id", id.toString()
				)
			);
	}

	@Override
	public Employee create(final EmployeeCreationObject newEmployee) 
			throws IllegalArgumentException {
		
		if (newEmployee == null) {
			throw new IllegalArgumentException(
				"EmployeeCreationObject can not be null"
			);
		}

		return employeeRepository.save(
			new Employee(
				newEmployee.getFirstName(),
				newEmployee.getLastName(),
				newEmployee.getRole(),
				newEmployee.getSalary()
			)
		);
	}
	
	@Override
	public Employee updateById(
			final EmployeeCreationObject newEmployee, final Long id)
			throws IllegalArgumentException {
		
		if (newEmployee == null) {
			throw new IllegalArgumentException(
				"EmployeeCreationObject can not be null"
			);
		}
		
		try {
			final Employee employee = findById(id);
			
			employee.setFirstName(newEmployee.getFirstName());
			employee.setLastName(newEmployee.getLastName());
			employee.setRole(newEmployee.getRole());
			employee.setSalary(newEmployee.getSalary());
			
			return employeeRepository.save(employee);
			
		} catch (ResourceNotFoundException ex) {
			return employeeRepository.save(
				new Employee(
					id, 
					newEmployee.getFirstName(),
					newEmployee.getLastName(),
					newEmployee.getRole(),
					newEmployee.getSalary()
				)
			);
		}
	}

	@Override
	public void deleteById(final Long id) throws IllegalArgumentException {
		if (id == null) {
			throw new IllegalArgumentException("Id can not be null");
		}
		employeeRepository.deleteById(id);
	}
}
