package com.dtmxtest.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dtmxtest.model.Employee;
import com.dtmxtest.repo.EmployeeRepository;

@RestController
public class WebController {
	@Autowired
	EmployeeRepository repository;
	
	@RequestMapping("/employees/seed")
	public String process(){
		repository.saveAll(Arrays.asList(new Employee("Alex", "Behrman", "alex@email.com"), new Employee("Emily", "Davitt", "emily@email.com"),
										new Employee("Cal", "Notman", "cal@email.com"), new Employee("Shashank", "Singh", "shashank@email.com")));
		return "Done";
	}
	
	/**
	 * get list of all employees from db
	 * @return elist object
	 */
	@GetMapping("/employees")
	public List<Employee> getAll() {
		List<Employee> elist = (List<Employee>) repository.findAll();
		return elist;
	}
    
	/**
	 * get specific employee
	 * @param id
	 * @return employee object
	 */
	@GetMapping("/employees/{id}")
	public Employee getById(@PathVariable Long id) {
		Employee e1 = repository.getById(id);
    	if (e1 == null) {
    		throw new IllegalArgumentException("No employee with that id");
    	}
		return e1;
	}
    
    /**
     * add new employee to db
     * @param e
     * @return new employee object
     */
    @PostMapping("/employees")
    public Employee createEmployee(@RequestBody Employee e) {
		Employee e2 = new Employee(e.getFirstName(), e.getLastName(), e.getEmail());
		repository.save(e2);
    	return e2;
    }
    
    /**
     * update info for current employee
     * @param lastName
     * @param e
     * @return updated employee object
     */
    @PutMapping("/employees/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee e) {
    	Employee e3 = repository.getById(id);
    	if (e3 == null) {
    		throw new IllegalArgumentException("No employee with that id");
    	}
    	
    	if (e.getFirstName() != null) {
    		e3.setFirstName(e.getFirstName());
    	}
    	if (e.getLastName() != null) {
    		e3.setLastName(e.getLastName());
    	}
    	if (e.getEmail() != null) {
    		e3.setEmail(e.getEmail());
    	}
    	
    	repository.save(e3);
    	return e3;
    }
    
    /**
     * delete employee from db
     * @param lastName
     * @return string confirming employee deletion
     */
    @DeleteMapping("/employees/{id}")
    public String deleteEmployee(@PathVariable Long id) {
		Employee e4 = repository.getById(id);
    	if (e4 == null) {
    		throw new IllegalArgumentException("No employee with that id");
    	}
    	
		repository.delete(e4);
		return "Employee " + e4.getFirstName() + " " + e4.getLastName() + " deleted";
    }
    
    /**
     * delete entire repository
     * @return string confirming deletion
     */
    @DeleteMapping("/employees/all")
    public String deleteAll() {
    	repository.deleteAll();
    	return "Repository deleted";
    }
	
}