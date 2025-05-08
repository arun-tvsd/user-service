package com.arun.user_service.controllers.exceptions;

public class UserNotFoundException extends Exception{

    public UserNotFoundException(String msg) {
        super(msg);
    }
    
}
