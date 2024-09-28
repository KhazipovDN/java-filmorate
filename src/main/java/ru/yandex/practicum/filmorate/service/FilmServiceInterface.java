package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmServiceInterface {
    void likeFilm(Integer userId, Integer filmId);

    void unlikeFilm(Integer userId, Integer filmId);

    List<Film> getTopFilms(Integer count);

    Film getFilmById (Integer filmId);
}
