package com.dtmxtest.repo;

import org.springframework.data.repository.CrudRepository;

import com.dtmxtest.model.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Long>{
	Employee findByLastName(String lastName);
	Employee getById(Long id);
}