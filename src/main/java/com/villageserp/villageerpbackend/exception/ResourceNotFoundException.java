package com.villageserp.villageerpbackend.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object id) {
        super("ไม่พบ " + resource + " : " + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
