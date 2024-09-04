package com.example.demo.controller;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Employee")
@Data
public class Employee {
//https://www.youtube.com/watch?v=eh9C0GyxtHE
    @Id
//   @GeneratedValue(strategy = GenerationType.AUTO)
    

//@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_generator")
//@SequenceGenerator(name="book_generator", sequenceName = "book_seq", allocationSize=1)

    private long  empId;
    private String name;
    private String email;
    private String gender;
    private boolean isActive;
    private int phoneNumber;

    
}
