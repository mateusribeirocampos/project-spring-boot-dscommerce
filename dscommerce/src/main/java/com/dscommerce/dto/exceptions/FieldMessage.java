package com.dscommerce.dto.exceptions;

public class FieldMessage {

    private String fieldMessage;
    private String message;

    public FieldMessage() {}

    public FieldMessage(String fieldMessage, String message) {
        this.fieldMessage = fieldMessage;
        this.message = message;
    }

    public String getFieldMessage() {
        return fieldMessage;
    }

    public String getMessage() {
        return message;
    }
}
