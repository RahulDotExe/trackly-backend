package com.trackly.trackly_backend.user;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String message){
        super(message);

    }
}
