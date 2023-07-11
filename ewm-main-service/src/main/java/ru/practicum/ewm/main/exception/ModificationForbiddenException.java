package ru.practicum.ewm.main.exception;

public class ModificationForbiddenException extends RuntimeException {
    public ModificationForbiddenException(String message) {
        super(message);
    }
}
