package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.demo.controller.Employee;
import com.example.demo.controller.MyRepositry;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@AutoConfiguration
public class SampleMavenApplication {
    @Autowired
    private MyRepositry serviceClass;
    
    @PostConstruct
    public  void saveDefaultUser(){
        for(int i=0;i<8;i++) {
            Employee employee=new Employee();
            employee.setEmpId(i);
            employee.setName("raju");
            employee.setEmail("raju@gmail.com");
            serviceClass.save(employee);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SampleMavenApplication.class, args);
    }

//    @Bean // for filter ratelimiter
//    ProxyManager<String> proxyManager(CacheManager cacheManager) {
//        return new JCacheProxyManager<>(cacheManager.getCache("cache"));
//    }

    //https://www.youtube.com/watch?v=t1_RbTb3BUo
    //	public HttpTraceRepositry httpTraces() { //for custor actuator 
    //	    return new InMemoryHttpTraceRepositry();
    //	}
}
