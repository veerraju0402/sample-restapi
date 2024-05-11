package com.example.demo.controller.exception;

public class MyCustomException extends  RuntimeException{

    
    public MyCustomException(long id) {
        super("User(s) not found exception:"+id);
    }
}
