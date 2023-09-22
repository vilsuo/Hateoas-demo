
package com.example.demo.employee;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor @Data
public class Employee {

	@Id
	@GeneratedValue
	private Long id;
	
	private String firstName;
	
	private String lastName;
	
	private String role;
	
	private Double salary;

	public Employee(String firstName, String lastName, String role, Double salary) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = role;
		this.salary = salary;
	}
}