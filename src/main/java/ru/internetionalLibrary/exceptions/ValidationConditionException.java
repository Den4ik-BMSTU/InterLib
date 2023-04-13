package ru.internetionalLibrary.exceptions;

public class ValidationConditionException extends RuntimeException {
    public ValidationConditionException(final String message) {
        super(message);
    }
}
