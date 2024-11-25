package ru.yandex.practicum.filmorate.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;
import java.util.Map;

public interface FilmStorage {
    void createFilm(Film film);

    void updateFilm(Film film);

    Map<Integer, Film> getAllFilms();

    Boolean findById(Film film);

    Film getFilmsById(Integer filmId);

    void deleteFilm(Film film);

    List<Genre> getAllGenres();

    Genre getGenreById(Integer id);

    List<MPA> getAllRatings();

    MPA getRatingById(Integer id);
}
