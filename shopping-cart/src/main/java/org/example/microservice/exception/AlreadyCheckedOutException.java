package org.example.microservice.exception;

public class AlreadyCheckedOutException extends RuntimeException{
    public AlreadyCheckedOutException (String message){
        super(message);
    }
}
