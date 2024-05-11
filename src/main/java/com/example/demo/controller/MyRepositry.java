package com.example.demo.controller;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MyRepositry  extends JpaRepository<Employee, Long> {}
