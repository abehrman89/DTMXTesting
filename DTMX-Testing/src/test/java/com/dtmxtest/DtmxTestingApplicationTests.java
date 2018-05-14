package com.dtmxtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import com.dtmxtest.controller.WebController;
import com.dtmxtest.model.Employee;
import com.dtmxtest.repo.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@RunWith(SpringRunner.class)
@WebMvcTest(WebController.class)
public class DtmxTestingApplicationTests {
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private EmployeeRepository repository;
	
	@Before
	public void setUp() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void testSeed() throws Exception {
		mvc.perform(get("/employees/seed")).andExpect(content().string("Done"));
	}
	
	@Test
	public void getAll_ValidEmployees_returnAll() throws Exception {
		Employee e1 = new Employee("firstName1", "lastName1", "email@1");
		Employee e2 = new Employee("firstName2", "lastName2", "email@2");
		Employee e3 = new Employee("firstName3", "lastName3", "email@3");
		List<Employee> allEmployees = Arrays.asList(e1, e2, e3);
		repository.saveAll(allEmployees);
		Mockito.when(repository.findAll()).thenReturn(allEmployees);
		
		MvcResult result = (MvcResult) this.mvc.perform(get("/employees")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		
		ObjectWriter ow = new ObjectMapper().writer();
		String json = ow.writeValueAsString(allEmployees);
		
		assertEquals(result.getResponse().getContentAsString(), json);
	}
	
	@Test
	public void getAll_noEmployees_returnEmpty() throws Exception {
		List<Employee> allEmployees = Arrays.asList();
		Mockito.when(repository.findAll()).thenReturn(allEmployees);
		
		MvcResult result = (MvcResult) this.mvc.perform(get("/employees")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		
		ObjectWriter ow = new ObjectMapper().writer();
		String json = ow.writeValueAsString(allEmployees);
		
		assertEquals(result.getResponse().getContentAsString(), json);
	}
	
	@Test
	public void getById_validId_returnEmployee() throws Exception {
		Employee e4 = new Employee("firstName4", "lastName4", "email@4");
		Employee e5 = new Employee("firstName5", "lastName5", "email@5");
		repository.save(e4);
		repository.save(e5);
		
		Mockito.when(repository.getById(e4.getId())).thenReturn(e4);
		//Mockito.when(repository.getById(e5.getId())).thenReturn(e5);
		
		MvcResult result = (MvcResult) this.mvc.perform(get("/employees/{id}", e4.getId())
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		
		ObjectWriter ow = new ObjectMapper().writer();
		String json = ow.writeValueAsString(e4);
		//String json = ow.writeValueAsString(e55);
		
		assertEquals(result.getResponse().getContentAsString(), json);
	}
	
	@Test(expected = NestedServletException.class)
	public void getById_invalidId_throwException() throws Exception {
		MvcResult result = (MvcResult) this.mvc.perform(get("/employees/{id}", 12L)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
	}
	
	@Test
	public void createEmployee_validInput_returnEmployee() throws Exception {
		Employee e6 = new Employee("firstName6", "lastName6", "email@6");
		ObjectWriter ow = new ObjectMapper().writer();
		String json = ow.writeValueAsString(e6);
		MvcResult result = (MvcResult) this.mvc.perform(post("/employees")
				.contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
		assertEquals(json, result.getResponse().getContentAsString());		
	}
	
	/*
	//i don't think this can be done
	@Test
	public void createEmployee_invalidInput_throwException() throws Exception {
		
	}
	*/

	@Test
	public void createEmployee_nullInput_returnEmployee() throws Exception {
		Employee e7 = new Employee(null, null, null);
		ObjectWriter ow = new ObjectMapper().writer();
		String json = ow.writeValueAsString(e7);
		MvcResult result = (MvcResult) this.mvc.perform(post("/employees")
				.contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
		assertEquals(json, result.getResponse().getContentAsString());	
	}
	
	@Test
	public void createEmployee_duplicateInput_throwException() throws Exception {
		Employee e8 = new Employee("firstName8", "lastName8", "email@8");
		long e8ID = new Random().nextLong();
		e8.setId(e8ID);
		repository.save(e8);
		ObjectWriter ow1 = new ObjectMapper().writer();
		String json1 = ow1.writeValueAsString(e8);
		
		Employee e9 = new Employee("firstName8", "lastName8", "email@8");
		ObjectWriter ow2 = new ObjectMapper().writer();
		String json2 = ow2.writeValueAsString(e9);
		MvcResult result2 = (MvcResult) this.mvc.perform(post("/employees")
				.contentType(MediaType.APPLICATION_JSON).content(json2)).andReturn();
		
		assertEquals(json2, result2.getResponse().getContentAsString());
		assertNotEquals(json1, json2);
		assertNotEquals(json1, result2.getResponse().getContentAsString());
		
	}
	
	@Test
	public void updateEmployee_firstName_returnEmployee() throws Exception {
		Employee e10 = new Employee("firstName10", "lastName10", "email@10");
		
		ObjectWriter ow = new ObjectMapper().writer();
		String json = ow.writeValueAsString(e10);
		
		Mockito.when(repository.getById(e10.getId())).thenReturn(e10);
		mvc.perform(post("/employees").contentType(MediaType.APPLICATION_JSON)
				.content(json)).andReturn();
		
		Employee temp = new Employee("newFirstName", null, null);
		ObjectWriter ow1 = new ObjectMapper().writer();
		String json1 = ow1.writeValueAsString(temp);
		
		MvcResult result = (MvcResult) this.mvc.perform(put("/employees/{id}", e10.getId())
				.contentType(MediaType.APPLICATION_JSON).content(json1)).andReturn();
		
		e10.setFirstName("newFirstName");
		ObjectWriter ow2 = new ObjectMapper().writer();
		String json2 = ow2.writeValueAsString(e10);
		
		assertEquals(result.getResponse().getContentAsString(), json2);	
	}
	
	@Test
	public void updateEmployee_lastName_returnEmployee() throws Exception {
		Employee e11 = new Employee("firstName11", "lastName11", "email@11");
		
		ObjectWriter ow = new ObjectMapper().writer();
		String json = ow.writeValueAsString(e11);
		
		Mockito.when(repository.getById(e11.getId())).thenReturn(e11);
		mvc.perform(post("/employees").contentType(MediaType.APPLICATION_JSON)
				.content(json)).andReturn();
		
		Employee temp = new Employee(null, "newLastName", null);
		ObjectWriter ow1 = new ObjectMapper().writer();
		String json1 = ow1.writeValueAsString(temp);
		
		MvcResult result = (MvcResult) this.mvc.perform(put("/employees/{id}", e11.getId())
				.contentType(MediaType.APPLICATION_JSON).content(json1)).andReturn();
		
		e11.setLastName("newLastName");
		ObjectWriter ow2 = new ObjectMapper().writer();
		String json2 = ow2.writeValueAsString(e11);
		
		assertEquals(result.getResponse().getContentAsString(), json2);	
	}
	
	@Test
	public void updateEmployee_email_returnEmployee() throws Exception {
		Employee e12 = new Employee("firstName12", "lastName12", "email@12");
		
		ObjectWriter ow = new ObjectMapper().writer();
		String json = ow.writeValueAsString(e12);
		
		Mockito.when(repository.getById(e12.getId())).thenReturn(e12);
		mvc.perform(post("/employees").contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
		
		Employee temp = new Employee(null, null, "newEmail");
		ObjectWriter ow1 = new ObjectMapper().writer();
		String json1 = ow1.writeValueAsString(temp);
		
		MvcResult result = (MvcResult) this.mvc.perform(put("/employees/{id}", e12.getId())
				.contentType(MediaType.APPLICATION_JSON).content(json1)).andReturn();
		
		e12.setEmail("newEmail");
		ObjectWriter ow2 = new ObjectMapper().writer();
		String json2 = ow2.writeValueAsString(e12);
		
		assertEquals(result.getResponse().getContentAsString(), json2);	
	}
	
	@Test
	public void updateEmployee_none_returnEmployee() throws Exception {
		Employee e13 = new Employee("firstName13", "lastName13", "email@13");
		
		ObjectWriter ow = new ObjectMapper().writer();
		String json = ow.writeValueAsString(e13);
		
		Mockito.when(repository.getById(e13.getId())).thenReturn(e13);
		mvc.perform(post("/employees").contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
		
		Employee temp = new Employee(null, null, null);
		ObjectWriter ow1 = new ObjectMapper().writer();
		String json1 = ow1.writeValueAsString(temp);
		
		MvcResult result = (MvcResult) this.mvc.perform(put("/employees/{id}", e13.getId())
				.contentType(MediaType.APPLICATION_JSON).content(json1)).andReturn();
		
		assertEquals(result.getResponse().getContentAsString(), json);	
	}
	
	@Test
	public void deleteEmployee_validID_confirmDeletion() throws Exception {
		Employee e14 = new Employee("firstName14", "lastName14", "email@14");
		e14.setId(12L);
		repository.save(e14);
		String confirmation = "Employee " + e14.getFirstName() + " " + e14.getLastName() + " deleted";
		
		Mockito.when(repository.getById(e14.getId())).thenReturn(e14);
		MvcResult result = (MvcResult) this.mvc.perform(delete("/employees/{id}", e14.getId())
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		
		assertEquals(result.getResponse().getContentAsString(), confirmation);
		
	}
	
	@Test(expected = NestedServletException.class)
	public void deleteEmployee_invalidID_throwException() throws Exception {
		mvc.perform(delete("/employees/{id}", 15L)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
	}
	
	@Test
	public void deleteAll_fullDB_returnConfirmation() throws Exception {
		Employee e1 = new Employee("firstName1", "lastName1", "email@1");
		Employee e2 = new Employee("firstName2", "lastName2", "email@2");
		Employee e3 = new Employee("firstName3", "lastName3", "email@3");
		List<Employee> allEmployees = Arrays.asList(e1, e2, e3);
		repository.saveAll(allEmployees);
		
		mvc.perform(delete("/employees/all")).andExpect(content().string("Repository deleted"));
	}
	
	@Test
	public void deleteAll_emptyDB_returnConfirmation() throws Exception {		
		mvc.perform(delete("/employees/all")).andExpect(content().string("Repository deleted"));
	}
}
