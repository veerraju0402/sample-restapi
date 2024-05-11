package com.example.demo.controller.exception;

public class MyCustomException2 extends  RuntimeException{

    
    public MyCustomException2(long id) {
        super("User(s) not found exception:"+id);
    }
}
