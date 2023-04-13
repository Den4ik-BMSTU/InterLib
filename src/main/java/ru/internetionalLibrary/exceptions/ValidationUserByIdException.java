package ru.internetionalLibrary.exceptions;

public class ValidationUserByIdException extends RuntimeException {
    public ValidationUserByIdException(final String message) {
        super(message);
    }
}
