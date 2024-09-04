//package com.example.demo.filter.rateLimiting;
//
//import java.time.Duration;
//import java.util.Optional;
//import java.util.function.Supplier;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.example.demo.controller.Employee;
//import com.example.demo.controller.ServiceClass;
//
//import io.github.bucket4j.Bandwidth;
//import io.github.bucket4j.Bucket;
//import io.github.bucket4j.BucketConfiguration;
//import io.github.bucket4j.Refill;
//import io.github.bucket4j.distributed.proxy.ProxyManager;
//
//@Service
//public class RateLimiter {
//
//    @Autowired
//    ServiceClass userService;
//    
//    @Autowired
//    ProxyManager<String> proxyManager;
//    
//    public Bucket resolveBucket(String key) {
//        Supplier<BucketConfiguration> configSupplier = getConfigSupplierForUser(key);
//        
//        return proxyManager.builder().build(key, configSupplier);
//    }
//
//    private Supplier<BucketConfiguration> getConfigSupplierForUser(String userId) {
//        Optional<Employee> emppl = userService.getEmployee(Long.valueOf(userId));
//        Employee emp=emppl.get();
//        //long noOfReqs=emp.getLimit(); //no of req to be taken
//        
//        Refill refill = Refill.intervally(5 /* noOfReqs */, Duration.ofMinutes(1));
//        Bandwidth limit = Bandwidth.classic(5 /* noOfReqs */, refill);
//        return () -> (BucketConfiguration.builder()
//                .addLimit(limit)
//                .build());
//    }
// 
//}