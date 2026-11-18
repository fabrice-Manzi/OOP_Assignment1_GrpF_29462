package com.auca.exceptions;

public class prerequisiteMissingException extends Exception {
    private static final long serialVersionUID = 1L;

    public prerequisiteMissingException(String message) {
        super(message);
    }

}
