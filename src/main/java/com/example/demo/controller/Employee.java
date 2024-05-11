package com.example.demo.controller;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Employee")
@Data
public class Employee {
//https://www.youtube.com/watch?v=eh9C0GyxtHE
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long  empId;
    private String name;
    private String email;
    private String gender;
    private boolean isActive;
    private int phoneNumber;

    
}
