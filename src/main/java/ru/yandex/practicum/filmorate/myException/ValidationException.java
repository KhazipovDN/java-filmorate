package ru.yandex.practicum.filmorate.myException;

import java.util.List;

public class ValidationException extends RuntimeException {
    private final List<String> errorMessages;

    public ValidationException(List<String> errorMessages) {
        super("Validation errors occurred");
        this.errorMessages = errorMessages;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}