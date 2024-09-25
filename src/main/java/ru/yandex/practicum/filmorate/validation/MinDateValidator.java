package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.*;
import java.time.LocalDate;

public class MinDateValidator implements ConstraintValidator<MinDate, LocalDate> {
    private LocalDate minDate = LocalDate.of(1895, 12, 28);

    @Override
    public void initialize(MinDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null  || !value.isBefore(minDate);

    }
}