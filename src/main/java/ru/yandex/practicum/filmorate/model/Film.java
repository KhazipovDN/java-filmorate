package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.validation.MinDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Film.
 */
@Getter
@Setter
public class Film {
    private int id;

    @Size(min = 1, message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание фильма не может быть длиннее 200 символов")
    private String description;

    @NotNull(message = "Дата релиза обязательна")
    @Past(message = "Дата релиза не может быть в будущем")
    @MinDate
    private LocalDate releaseDate;

    private Set<Integer> likes = new HashSet<>();
    private List<Genre> genres = new ArrayList<>();
    private MPA mpa;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;


}
