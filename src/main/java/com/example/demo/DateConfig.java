package com.example.demo;

import java.util.Date;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DateConfig {

    @Bean("date")
//    @Profile("dev")
//    @Profile("qa")
    public Date  date(){
        return new Date();
    }
}