package ru.practicum.ewm.stats.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final Exception e) {
        log.error(String.format("Error: %s", e.getMessage()));
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus
    public ErrorResponse handle(final Throwable e) {
        log.error(String.format("Error: %s", e.getMessage()));
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}
