package ru.yandex.practicum.filmorate.myException;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}