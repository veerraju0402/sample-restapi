package com.example.demo.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.aopadvice.TrackExecutionTime;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;

@RestController
//@CrossOrigin("http://localhost:3000")
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

    @TrackExecutionTime
    @GetMapping("/now")
    public Date dkd() {
        Date testBean = (Date) applicationContext.getBean("date");
//        System.out.println("testBean:" + testBean);
        return testBean;
    }

    @GetMapping(value = "/msg")
    public String welCome() {
        return "welcome to java programming";
    }

    @GetMapping(value = "/employee")
    public Optional<Employee> firstService(@RequestParam long id) {
        return serviceClass.getEmployee(id);
    }
    
    @GetMapping(value = "/employee1")
    public Employee firstService1(@RequestParam long id) {
        return serviceClass.getEmployee1(id);
    }
    
    @GetMapping(value = "/employee2")
    public Employee firstService2(@RequestParam long id) {
        return serviceClass.getEmployee2(id);
    }
    
    @GetMapping(value = "/employee3/{id}")
    public Employee firstService3(@PathVariable long id) {
        return serviceClass.getEmployee2(id);
    }

    // https://www.youtube.com/watch?v=0Y1ECAeuw3I
    @RequestMapping(value = "/employee", method = RequestMethod.POST)
    public Employee secondService(@RequestBody Employee employee) {
//        System.out.println("Inside POST Method:" + employee);
        return serviceClass.saveEmployee(employee);
    }

    @TrackExecutionTime
    @GetMapping(value = "/employees")
    public List<Employee> firstService() {
        return serviceClass.getEmployees();
    }

    @DeleteMapping(value = "/employee")
    public String deleteService(@RequestParam long id) {
        serviceClass.deleteEmployee(id);
        return "Deleted";
    }

    @RequestMapping(value = "/employees", method = RequestMethod.POST)
    public List<Employee> secondService(@RequestBody List<Employee> employees) {
//        System.out.println("Inside POST Method2:" + employees);
        for (Employee employee : employees) {
            employee = serviceClass.saveEmployee(employee);
//            System.out.println("emp2:" + employee);
        }
        return employees;
    }

}
