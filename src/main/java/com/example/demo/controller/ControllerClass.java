package com.example.demo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ControllerClass {

	@Autowired
	ServiceClass serviceClass;
	@Autowired
	private ApplicationContext applicationContext;
	
	
    @GetMapping("/")
    @Hidden
    public void redirecting(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }

    @GetMapping("/now")
    public Date dkd(){
    	Date testBean = (Date) applicationContext.getBean("date");
    	System.out.println("testBean:"+testBean);
    	return testBean;
    }

	@GetMapping(value = "/msg")
	public String welCome() {
		return "welcome to java programming";
	}

	@GetMapping(value = "/employee")
	public Employee firstService(@RequestParam int id) {

		Employee emp = new Employee();
		emp.setName("emp1");
		emp.setDesignation("manager");
		emp.setEmpId(id);
		emp.setSalary(3000);
		return emp;
	}

	// https://www.youtube.com/watch?v=0Y1ECAeuw3I
	@RequestMapping(value = "/employee", method = RequestMethod.POST)
	public Employee secondService(@RequestBody Employee employee) {

		System.out.println("Inside POST Method:" + employee);
		Employee emp = new Employee();
		emp.setName(employee.getName());
		emp.setDesignation(employee.getDesignation());
		emp.setEmpId(employee.getEmpId());
		emp.setSalary(employee.getSalary());
		System.out.println("emp:" + emp);
		return emp;
	}

	@GetMapping(value = "/employees")
	public List<Employee> firstService() {
		List<Employee> employees = new ArrayList<>();
		Employee emp = new Employee();
		emp.setName("emp1");
		emp.setDesignation("manager");
		emp.setEmpId(1);
		emp.setSalary(3000);
		employees.add(emp);
		return employees;
	}

	@RequestMapping(value = "/employees", method = RequestMethod.POST)
	public List<Employee> secondService(@RequestBody List<Employee> employees) {
		System.out.println("Inside POST Method2:" + employees);
		for (Employee employee : employees) {
			Employee emp = new Employee();
			emp.setName(employee.getName());
			emp.setDesignation(employee.getDesignation());
			emp.setEmpId(employee.getEmpId());
			emp.setSalary(employee.getSalary());
			System.out.println("emp2:" + emp);
		}
		return employees;
	}

}
