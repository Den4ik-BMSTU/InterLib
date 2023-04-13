package ru.internetionalLibrary.exceptions;

public class EmptyResultFromDataBaseException extends RuntimeException {
    public EmptyResultFromDataBaseException(final String message) {
        super(message);
    }
}