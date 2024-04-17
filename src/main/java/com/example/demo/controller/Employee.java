package com.example.demo.controller;

public class Employee {
//https://www.youtube.com/watch?v=eh9C0GyxtHE
		private int empId;
		private String name;
		private String designation;
		private double salary;

		public Employee() {
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDesignation() {
			return designation;
		}

		public void setDesignation(String designation) {
			this.designation = designation;
		}

		public double getSalary() {
			return salary;
		}

		public void setSalary(double salary) {
			this.salary = salary;
		}

		public int getEmpId() {
			return empId;
		}

		public void setEmpId(int empId) {
			this.empId = empId;
		}

		@Override
		public String toString() {
			return "Employee [empId=" + empId + ", name=" + name + ", designation=" + designation + ", salary=" + salary
					+ "]";
		}



}
