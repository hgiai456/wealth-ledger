package com.giaidev.core.exception;

public class BusinessException extends RuntimeException{ /// Business Exception catch some error when user create account with an existed email
    public BusinessException(String message){
        super(message);
    }
}
