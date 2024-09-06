package com.example.demo.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.controller.exception.MyCustomException;
import com.example.demo.controller.exception.MyCustomException2;
import com.jmethods.catatumbo.EntityManager;
import com.jmethods.catatumbo.EntityManagerFactory;
import com.jmethods.catatumbo.EntityQueryRequest;
import com.jmethods.catatumbo.QueryResponse;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import reactor.netty.internal.util.MapUtils;

import com.google.api.core.ApiFuture;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.WriteResult;

@Service
public class ServiceClass {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceClass.class);

    @Autowired
    MyRepositry myRepositry;
//    @Autowired
//    GCPDataStore gCPDataStore;
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    @Autowired
    private ResourceLoader resourceLoader;
    
    @Value("${service.datastore.credentials}")
    private String credentials;

    @Value("${service.datastore.project-id}")
    private String projectId;

    @Value("${service.datastore.namespace}")
    private String namespace;
    
    @PostConstruct
    public void init() {
        EntityManagerFactory emf = EntityManagerFactory.getInstance();
        Resource resource = resourceLoader.getResource(credentials);
        try {
            entityManager = emf.createEntityManager(projectId, resource.getInputStream(), namespace);
        System.err.println("entityManager:"+entityManager);
        } catch (IOException e) {
            System.err.println("IOException while loading credentials resource: "+e.getMessage());
            LOGGER.error("IOException while loading credentials resource: {}", e.getMessage());
        }
    }

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
        return myRepositry.findAll(contactsPageable);
    }

    public List<Employee> getEmployee3(String id) {
        
//        try {   
//        FirestoreOptions firestoreOptions =
//                FirestoreOptions.getDefaultInstance().toBuilder()
//                    .setProjectId(projectId)
//                    .setCredentials(GoogleCredentials.getApplicationDefault())
//                    .build();
//            Firestore db = firestoreOptions.getService();
//            //to set default credentials
//            //https://cloud.google.com/docs/authentication/provide-credentials-adc
//            
//         // Create a Map to store the data we want to set
//            Map<String, Object> docData = new HashMap<>();
//            docData.put("name", "Los Angeles");
//            docData.put("email", "avr3@gmail.com");
//            docData.put("gender", "male");
//            docData.put("isActive", "false");
//            docData.put("empId", "1234");
//
//            // Add a new document (asynchronously) in collection "cities" with id "LA"
//            ApiFuture<WriteResult> future = db.collection("my-gcp.collection").document("LA").set(docData);
//            // ...
//            // future.get() blocks on response
//           
//                System.err.println("Update time : " + future.get().getUpdateTime());
//            }  catch (Exception e) {
//                System.err.println("error:"+e.getMessage());
//            }
        
//        List<Employee> irstGcpList=gCPDataStore.findByName(id);
//        System.out.println("irstGcpList:"+irstGcpList);

      StringBuilder selectQuery = new StringBuilder("select * from ").append("my-gcp.collection");
            EntityQueryRequest request = null;
            if (StringUtils.isEmpty(id)) {
                request = entityManager.createEntityQueryRequest(selectQuery.toString());
            } else {
                selectQuery.append(" where name = "+id);
                
                request = entityManager.createEntityQueryRequest(selectQuery.toString());
            }
            QueryResponse<Employee> response = entityManager.executeEntityQueryRequest(Employee.class, request);
            System.err.println("response:"+response);
            return response.getResults();
    }

}
