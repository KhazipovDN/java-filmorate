package ru.yandex.practicum.filmorate.myException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<List<String>> handleValidationException(ValidationException ex) {
        return new ResponseEntity<>(ex.getErrorMessages(), HttpStatus.BAD_REQUEST);
    }
}