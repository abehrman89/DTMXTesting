package com.dtmxtest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import com.dtmxtest.controller.WebController;
import com.dtmxtest.model.Employee;
import com.dtmxtest.repo.EmployeeRepository;

@RunWith(SpringRunner.class)
//@SpringBootTest
@WebMvcTest(WebController.class)
//@WebAppConfiguration
public class DtmxTestingApplicationTests {
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private EmployeeRepository repository;
	
	@Before
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void testSeed() throws Exception {
		Employee e1 = new Employee("a", "b", "c");
		repository.save(e1);
		mvc.perform(get("/employees/seed")).andExpect(content().string("Done"));
	}
	
	@Test
	public void getAll_ValidEmployees_returnAll() throws Exception {
		Employee e1 = new Employee("firstName1", "lastName1", "email@1");
		Employee e2 = new Employee("firstName2", "lastName2", "email@2");
		Employee e3 = new Employee("firstName3", "lastName3", "email@3");
		//repository.save(e1);
		List<Employee> allEmployees = Arrays.asList(e1, e2, e3);
		//given(repository.findAll()).willReturn(allEmployees);
		Mockito.when(repository.findAll()).thenReturn(allEmployees);
		mvc.perform(get("/employees"))
				.andExpect(jsonPath("$[0].firstName", is(e1.getFirstName())))
				.andExpect(jsonPath("$[1].lastName", is(e2.getLastName())))
				.andExpect(jsonPath("$[2].email", is(e3.getEmail())))
				.andExpect(jsonPath("$[1].id", is((int) e2.getId())));
	}
	
	/* FIGURE OUT HOW TO CHECK FOR NULL VALUE
	@Test
	public void getAll_noEmployees_returnEmpty() throws Exception {
		List<Employee> allEmployees = Arrays.asList();
		Mockito.when(repository.findAll()).thenReturn(allEmployees);
		mvc.perform(get("/employees"));
	}
	*/
	
	@Test
	public void getById_validId_returnEmployee() throws Exception {
		Employee e1 = new Employee("firstName1", "lastName1", "email@1");
		Employee e2 = new Employee("firstName2", "lastName2", "email@2");
		repository.save(e1);
		repository.save(e2);
		
		Mockito.when(repository.getById(e1.getId())).thenReturn(e1);
		//Mockito.when(repository.getById(e2.getId())).thenReturn(e2);

		//Employee found = repository.findByLastName("lastName1");
		//Employee found = repository.getById((long)12);

		//assertThat(found.getFirstName()).isEqualTo("firstName1");
		//assertThat(e1.getId()).isEqualTo(found.getId());
		//assertThat(found.getId()).isEqualTo(12).isEqualTo(12L).isEqualTo(e1.getId());
		
		//Employee found = repository.getById(e1.getId());
		//assertThat(found.getFirstName()).isEqualTo(e1.getFirstName());
		
		mvc.perform(get("/employees/{id}", e1.getId())).andExpect(jsonPath("$.firstName", is("firstName1")));
		//mvc.perform(get("/employees/{id}", e2.getId())).andExpect(jsonPath("$.lastName", is("lastName2")));
	}
	
	@Test(expected = NestedServletException.class)
	public void getById_invalidId_returnEmployee() throws Exception {
		mvc.perform(get("/employees/{id}", 12L)).andExpect(jsonPath("$.firstName", is("firstName1")));
	}
	
	@Test
	public void createEmployee_validInput_returnEmployee() throws Exception {
		
	}
	
}
