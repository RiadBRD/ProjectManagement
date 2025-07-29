package com.project.exceptions;

public class DuplicateProjectException extends RuntimeException{
    public DuplicateProjectException(String message) {
        super(message);
    }
}
