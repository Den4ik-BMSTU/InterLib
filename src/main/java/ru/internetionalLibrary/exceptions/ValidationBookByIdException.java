package ru.internetionalLibrary.exceptions;

public class ValidationBookByIdException extends RuntimeException {
    public ValidationBookByIdException(final String message) {
        super(message);
    }
}
