package com.dtmxtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.dtmxtest.repo.EmployeeRepository;

@SpringBootApplication
public class DtmxTestingApplication {
	
	@Autowired
	EmployeeRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(DtmxTestingApplication.class, args);
	}
}
