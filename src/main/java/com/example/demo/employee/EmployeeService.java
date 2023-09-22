
package com.example.demo.employee;

import com.example.demo.exception.ResourceNotFoundException;
import java.util.List;

public interface EmployeeService {
	
	public abstract List<Employee> findAll();
	
	/**
	 * Attempts to find an {@code Employee} with given {@code id}
	 * 
	 * @param id
	 *		the {@code id} of the {@code Employee} to look for
	 * @return 
	 *		the found {@code Employee}
	 * @throws IllegalArgumentException
	 *		if {@code id} is {@code null}
	 * @throws ResourceNotFoundException
	 *		if {@code Employee} is not found with given {@code id}
	 */
	public abstract Employee findById(Long id) 
			throws IllegalArgumentException, ResourceNotFoundException;
	
	/**
	 * Creates new {@code Employee} from {@code employee} and assigns it id
	 * 
	 * @param employee
	 *		the {@code Employee} to be created
	 * @return
	 *		the created {@code Employee} with assigned id
	 * @throws IllegalArgumentException
	 *		if {@code employee} is {@code null}
	 */
	public abstract Employee create(EmployeeCreationObject employee)
			throws IllegalArgumentException;
	
	/**
	 * If there exists a {@code Employee} with given {@code id}, then updates
	 * that {@code Employee} with values from {@code newEmployee}. Otherwise
	 * creates a new {@code Employee} from {@code newEmployee}
	 * 
	 * @param newEmployee
	 *		the new {@code Employee} to be created or the new values to update
	 * @param id
	 *		the id of the {@code Employee} to update
	 * @return
	 *		the created or updated {@code Employee}
	 * @throws IllegalArgumentException	
	 *		if {@code id} or {@code employee} is {@code null}
	 */
	public abstract Employee updateById(EmployeeCreationObject newEmployee, Long id)
			throws IllegalArgumentException;
	
	/**
	 * Deletes {@link Employee} by given {@code id}
	 * 
	 * @param id
	 *		the {@code id} of the {@code Employee} to delete
	 * @throws IllegalArgumentException
	 *		if {@code id} is {@code null}
	 */
	public abstract void deleteById(Long id) throws IllegalArgumentException;
	
}
