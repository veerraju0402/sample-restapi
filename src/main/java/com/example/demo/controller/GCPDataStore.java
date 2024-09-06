//package com.example.demo.controller;
//
//import java.util.List;
//
//import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
////import org.springframework.data.repository.query.Param;
//import org.springframework.cloud.gcp.data.datastore.repository.query.Query;
//
//public interface  GCPDataStore  extends DatastoreRepository<Employee, Long> {
//
//    @Query("select * from |com.example.demo.Employee| where name = @id")
//    List<Employee> findByName(String name);
//
//}
