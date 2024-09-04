package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.controller.exception.MyCustomException;
import com.example.demo.controller.exception.MyCustomException2;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@Service
public class ServiceClass {

    @Autowired
    MyRepositry myRepositry;

    public List<Employee> getEmployees() {
        return myRepositry.findAll();
    }

    public Optional<Employee> getEmployee(long id) {
        return myRepositry.findById(id);
    }

    public Employee getEmployee1(long id) {
        return myRepositry.findById(id).orElseThrow(() -> new MyCustomException(id));
    }

    public Employee getEmployee2(long id) {
        return myRepositry.findById(id).orElseThrow(() -> new MyCustomException2(id));
    }

    public Employee saveEmployee(Employee employee) {
        return myRepositry.save(employee);
    }

    public void deleteEmployee(long id) {
        myRepositry.deleteById(id);
    }

    public Page<Employee> findAll(Pageable contactsPageable) {
        // TODO Auto-generated method stub
        return myRepositry.findAll(contactsPageable);
    }

}
