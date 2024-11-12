package ru.yandex.practicum.filmorate.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {
    void createFilm(Film film);

    void updateFilm(Film film);

    Map<Integer, Film> getAllFilms();

    Boolean findById(Film film);

    Film getFilmsById(Integer filmId);

}
