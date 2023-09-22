
package com.example.demo.employee;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import org.springframework.stereotype.Component;

/*
This simple interface has one method: toModel(). It is based on converting a 
non-model object (Employee) into a model-based object (EntityModel<Employee>)
*/
@Component
public class EmployeeModelAssembler implements 
		RepresentationModelAssembler<Employee, EntityModel<Employee>> {
	
	final EmployeeController controller
			= methodOn(EmployeeController.class);
	
	@Override
	public EntityModel<Employee> toModel(final Employee employee) {
		final EntityModel<Employee> entityModel = EntityModel.of(
			employee,
			
			linkTo(controller.one(employee.getId()))
				.withSelfRel(),
				//.andAffordance(
				//	afford(
				//		controller.replaceEmployee(null, employee.getId())
				//	)
				//),
				
			//linkTo(controller.raise(employee.getId(), null))
			//	.withRel("raise"),
			
			
			//linkTo(controller.deleteEmployee(employee.getId()))
			//	.withRel("delete"),
			
			linkTo(controller.all())
				.withRel("employees")
		);
		
		return entityModel;
	}

	@Override
	public CollectionModel<EntityModel<Employee>> toCollectionModel(
			Iterable<? extends Employee> entities) {
		
		return RepresentationModelAssembler
				.super.toCollectionModel(entities)
					.add(
						linkTo(controller.all())
							.withSelfRel()
								
						// is this added correctly?
						.andAffordance(
							afford(controller.newEmployee(null))
						)
					);
	}
}
