package ru.yandex.practicum.filmorate.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Map;

public interface FilmStorage {
    void createFilm(Film film);

    void updateFilm(Film film);

    Map<Integer, Film> getAllFilms();

    Boolean findById(Film film);

    Film getFilmsById(Integer filmId);
}
