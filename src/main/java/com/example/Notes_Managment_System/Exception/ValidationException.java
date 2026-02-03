package com.example.Notes_Managment_System.Exception;

public class ValidationException extends RuntimeException {

    private final String code;
    private final String field;
    private final String description;

    public ValidationException(String message, String code, String field, String description) {
        super(message);
        this.code = code;
        this.field = field;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getField() {
        return field;
    }

    public String getDescription() {
        return description;
    }
}
