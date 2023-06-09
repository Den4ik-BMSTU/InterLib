package ru.internetionalLibrary.controllers;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.internetionalLibrary.exceptions.*;
import ru.internetionalLibrary.models.ErrorResponse;
import ru.internetionalLibrary.exceptions.*;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ValidationException.class
            , EmptyResultFromDataBaseException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleValidationException(final Exception e) {
        return new ErrorResponse("В базе нет данных: " + e.getMessage());
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameterException(IncorrectParameterException e) {
        return new ErrorResponse("Ошибка с полем: " + e.getParameter());
    }

    @ExceptionHandler({DataIntegrityViolationException.class
            , ValidationUserByIdException.class
            , ValidationBookByIdException.class
            , MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataIntegrityViolationException(Exception e) {
        return new ErrorResponse("Неверный запрос: " + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleValidationConditionException(final ValidationConditionException e) {
        return new ErrorResponse("Ошибка на стороне сервера: " + e.getMessage());
    }
}
