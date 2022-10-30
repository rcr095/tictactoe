package com.challenge.tictactoe.config;

import javax.management.InvalidAttributeValueException;
import javax.naming.NameNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.challenge.tictactoe.model.CustomError;

@ControllerAdvice
public class ControllerAdviceConfig {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ExceptionHandler(NameNotFoundException.class)
    public CustomError notFoundException(NameNotFoundException ex) {
        return new CustomError("Game ID not found");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(InvalidAttributeValueException.class)
    public CustomError invalidAttributesException(InvalidAttributeValueException ex) {
        return new CustomError(ex.getMessage());
    }
}
