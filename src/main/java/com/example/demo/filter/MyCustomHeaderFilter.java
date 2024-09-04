package com.example.demo.filter;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

//@Configuration
//@RequiredArgsConstructor
//@Slf4j
public class MyCustomHeaderFilter extends OncePerRequestFilter  {
    private static final int BUFFER_SIZE = 128;
    
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        System.err.println("2/3 " + MyCustomHeaderFilter.class.getSimpleName());
        
//        Enumeration<String> headers = request.getHeaders("testHead");
//        System.err.println("headers:"+Optional.ofNullable(headers.nextElement()).get());
        
        request = new CustomRequestWrapper((HttpServletRequest) request);
        
        
        HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
           
            final String myCustomHeaderName = "Custom-Header";
            final String myCustomHeaderValue = "FooBar";

            @Override
            public Enumeration<String> getHeaderNames() {
                List<String> headerNames = Collections.list(super.getHeaderNames());
                headerNames.add(myCustomHeaderName);
                return Collections.enumeration(headerNames);
            }

            @Override
            public Enumeration<String> getHeaders(String name) {
                if (name.equals(myCustomHeaderName)) {
                    return Collections.enumeration(Collections.singletonList(myCustomHeaderValue));
                }
                return super.getHeaders(name);
            }

            @Override
            public String getHeader(String name) {
                if (name.equals(myCustomHeaderName)) {
                    return myCustomHeaderValue;
                }
                return super.getHeader(name);
            }
        };
        
        chain.doFilter(wrappedRequest, response);
    }
    
    
}