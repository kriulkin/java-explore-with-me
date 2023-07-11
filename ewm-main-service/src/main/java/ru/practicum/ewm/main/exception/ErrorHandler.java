package ru.practicum.ewm.main.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(value = NoSuchEntityException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handle(final NoSuchEntityException e) {
        log.error(String.format("Error: %s", e.getMessage()));
        return new ApiError(
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList()),
                e.getMessage(),
                "Not Found Error",
                HttpStatus.NOT_FOUND,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            ArgumentValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(Exception e) {
        log.error(String.format("Error: %s", e.getMessage()));
        return new ApiError(
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList()),
                e.getMessage(),
                "Argument not valid",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(value = ModificationForbiddenException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handle(final ModificationForbiddenException e) {
        log.error(String.format("Error: %s", e.getMessage()));
        return new ApiError(
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList()),
                e.getMessage(),
                "Not Found Error",
                HttpStatus.CONFLICT,
                LocalDateTime.now()
        );
    }

    public ApiError handle(final Throwable e) {
        log.error(String.format("Error: %s", e.getMessage()));
        return new ApiError(
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList()),
                e.getMessage(),
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now()
        );
    }
}
