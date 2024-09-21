package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import com.example.demo.controller.Employee;
import com.example.demo.controller.MyRepositry;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
//@PropertySources({
//    @PropertySource(value = "mysql-configMap.yaml"),
//    @PropertySource(value = "app-deployment.yaml"),
//    @PropertySource(value = "db-deployment.yml")})
//@AutoConfiguration
public class SampleMavenApplication {
	@Autowired
	private MyRepositry serviceClass;

	@PostConstruct
	public void saveDefaultUser() {
		for (int i = 1; i < 8; i++) {
			Employee employee = new Employee();
			employee.setEmpId(i);
			employee.setName("raju");
			employee.setEmail("raju@gmail.com");
			serviceClass.save(employee);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(SampleMavenApplication.class, args);

//        Runtime.getRuntime().addShutdownHook(new Thread() 
//        { 
//          public void run() 
//          { 
//            System.out.println("Shutdown Hook is running !"); 
//            SampleMavenApplication te=new SampleMavenApplication();
//            te.deleteCall();
//            System.out.println("Application Terminating ..."); 
//          } 
//        }); 

	}

	public void deleteCall() {
		serviceClass.deleteAll();
	}

//    @Bean // for filter ratelimiter
//    ProxyManager<String> proxyManager(CacheManager cacheManager) {
//        return new JCacheProxyManager<>(cacheManager.getCache("cache"));
//    }

	// https://www.youtube.com/watch?v=t1_RbTb3BUo
	// public HttpTraceRepositry httpTraces() { //for custor actuator
	// return new InMemoryHttpTraceRepositry();
	// }
}
