package com.recyclewise.exception;

/**
 * Thrown when a requested domain entity cannot be found.
 *
 * OOP  — Encapsulation: bundles resource name, field, and value into one object
 * SOLID — (S) Single Responsibility: exists solely to represent a "not found" failure
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() { return resourceName; }
    public String getFieldName()    { return fieldName; }
    public Object getFieldValue()   { return fieldValue; }
}
