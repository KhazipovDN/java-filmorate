package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.film.FilmStorage;
import ru.yandex.practicum.filmorate.user.UserStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.myException.InternalServerErrorException;
import ru.yandex.practicum.filmorate.myException.ResourceNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService implements FilmServiceInterface {

    @Autowired(required = true)
    private FilmStorage filmStorage;
    @Autowired(required = true)
    private UserStorage userStorage;


    @Override
    public void likeFilm(Integer userId, Integer filmId) {
        Film film = filmStorage.getAllFilms().get(filmId);
        User user = userStorage.getAllUsers().get(userId);
        if (film == null || user == null) {
            throw new ResourceNotFoundException("Фильм или пользователь не найден");
        }
        if (film.getLiked().contains(userId)) {
            throw new InternalServerErrorException("Пользователь уже поставил лайк этому фильму");
        } else {
            film.getLiked().add(userId);
        }
    }

    @Override
    public void unlikeFilm(Integer userId, Integer filmId) {
        Film film = filmStorage.getAllFilms().get(filmId);
        User user = userStorage.getAllUsers().get(userId);
        if (film == null || user == null) {
            throw new ResourceNotFoundException("Фильм или пользователь не найден");
        }
        if (film.getLiked().contains(userId)) {
            film.getLiked().remove(userId);
        } else {
            throw new InternalServerErrorException("Пользователь не ставил лайк этому фильму");
        }
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        List<Film> allFilms = new ArrayList<>(filmStorage.getAllFilms().values());
        allFilms.sort(Comparator.comparingInt(film -> -film.getLiked().size()));
        if (count == null) {
            count = 10;
        }
        return allFilms.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film getFilmById(Integer filmId) {
        if (!filmStorage.getAllFilms().containsKey(filmId))
            throw new ResourceNotFoundException("Фильм не найден");
        return filmStorage.getAllFilms().get(filmId);
    }
}
