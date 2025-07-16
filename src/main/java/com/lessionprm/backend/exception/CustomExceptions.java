package com.lessionprm.backend.exception;

class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

class CourseNotFoundException extends ResourceNotFoundException {
    public CourseNotFoundException(String message) {
        super(message);
    }
}

class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

class PaymentNotFoundException extends ResourceNotFoundException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}

class InvoiceNotFoundException extends ResourceNotFoundException {
    public InvoiceNotFoundException(String message) {
        super(message);
    }
}

class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}

class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}