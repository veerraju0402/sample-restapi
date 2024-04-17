package com.example.demo;

import java.util.Date;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DateConfig {

    @Bean("date")
    public Date  date(){
        return new Date();
    }
}