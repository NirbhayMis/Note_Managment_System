package com.example.Notes_Managment_System.Exception;

public class ResourceNotFoundException extends RuntimeException {

    private final String code;
    private final String field;
    private  final String descrption;
    public ResourceNotFoundException(String code, String message ,String field,String descrption) {
        super(message);
        this.code = code;
        this.field=field;
        this.descrption=descrption;

    }

    public String getCode() {
        return code;
    }
    public String getField() {
        return field;
    }
    public String getDescrption(){
        return descrption;
    }
}
