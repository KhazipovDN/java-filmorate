package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.myException.ResourceNotFoundException;

import java.util.List;
import java.util.Map;

public interface FilmService {
    void likeFilm(Integer userId, Integer filmId);

    void unlikeFilm(Integer userId, Integer filmId);

    List<Film> getTopFilms(Integer count);

    Film getFilmById(Integer filmId);

}
