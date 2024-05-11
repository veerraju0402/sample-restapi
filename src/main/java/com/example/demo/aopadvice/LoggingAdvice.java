package com.example.demo.aopadvice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAdvice {

//    public static final Logger log = LoggerFactory.getLogger(LoggingAdvice.class.getName());;
//    private final Logger log = LoggerFactory.getLogger(LoggingAdvice.class);
    


//    @Pointcut(value = "execution(* com.example.demo..*(..))")
//    @Pointcut(value="execution(* com.example.demo.*.*.*(..) )")
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void myPointcut() {

    }

    // https://www.youtube.com/watch?v=RVvKPP5HyaA&list=RDCMUCORuRdpN2QTCKnsuEaeK-kQ&index=1
    @Around("myPointcut()")
    public Object applicationLogger(ProceedingJoinPoint pjp) throws Throwable {

//        System.out.println("The method aroundAdvice() before invocation of the method " + pjp.getSignature().getName()
//                + " method");
//        Object object =   pjp.proceed();
//        System.out.println(
//                "The method aroundAdvice() after invocation of the method " + pjp.getSignature().getName() + " method");
 
        ObjectMapper mapper = new ObjectMapper();
        String methodName = pjp.getSignature().getName();
        String className = pjp.getTarget().getClass().toString();
        Object[] array = pjp.getArgs();
        log.info("method invoked " + className + " : " + methodName + "()" + "arguments : "
                + mapper.writeValueAsString(array));
        Object object = pjp.proceed();
        log.info(className + " : " + methodName + "()" + "Response : " + mapper.writeValueAsString(object));
        return object;
    }

    // https://www.youtube.com/watch?v=2YTGgHkJg2M&list=RDCMUCORuRdpN2QTCKnsuEaeK-kQ&start_radio=1&rv=2YTGgHkJg2M&t=697
    @Around("@annotation(com.example.demo.aopadvice.TrackExecutionTime)")
    public Object trackTime(ProceedingJoinPoint pjp) throws Throwable {
        long stratTime=System.currentTimeMillis();
        Object obj=pjp.proceed();
        long endTime=System.currentTimeMillis();
        System.out.println("Method name"+pjp.getSignature()+" time taken to execute : "+(endTime-stratTime));
        return obj;
    }

//    @Before("execution(* com.example.demo.controller.ServiceClass.*(..))")
//    public void printLogStatementsBefore() {
//        System.out.println(
//                ".............Looking for @Around advice, if none is there, @Before will be called first. My role is to execute before each and every method.............");
//    }
//
//    @After("execution(* com.example.demo.controller.ServiceClass.*(..))")
//    public void printLogStatementsAfter() {
//        System.out.println(
//                ".............Looking for @Around advice, if none is there, @After will be called after @Before(if available). My role is to execute after each and every method.............");
//    }
}