package com.example.todo.exception;

/**
 * Exception thrown when a requested Todo item is not found.
 */
public class TodoNotFoundException extends RuntimeException {

    /**
     * Constructs a new exception with the given message.
     *
     * @param message description of the error
     */
    public TodoNotFoundException(String message) {
        super(message);
    }
}
