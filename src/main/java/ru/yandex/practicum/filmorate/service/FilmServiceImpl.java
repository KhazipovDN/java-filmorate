package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.film.FilmStorage;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.myException.ValidationException;
import ru.yandex.practicum.filmorate.user.UserStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.myException.InternalServerErrorException;
import ru.yandex.practicum.filmorate.myException.ResourceNotFoundException;

import java.util.*;

@Service
public class FilmServiceImpl implements FilmService {

    @Autowired
    @Qualifier("filmDbStorage")
    private FilmStorage filmStorage;
    @Autowired
    @Qualifier("userDbStorage")
    private UserStorage userStorage;


    @Override
    public void likeFilm(Integer filmId, Integer userId) {
        Film film = filmStorage.getAllFilms().get(filmId);
        User user = userStorage.getAllUsers().get(userId);
        if (film == null || user == null) {
            throw new ResourceNotFoundException("Фильм или пользователь не найден");
        }
        if (film.getLikes().contains(userId)) {
            throw new ValidationException("Пользователь уже поставил лайк этому фильму");
        } else {
            Set<Integer> likes = film.getLikes();
            likes.add(userId);
            film.setLikes(likes);
            filmStorage.updateFilm(film);
        }
    }

    @Override
    public void unlikeFilm(Integer filmId, Integer userId) {
        Film film = filmStorage.getAllFilms().get(filmId);
        User user = userStorage.getAllUsers().get(userId);
        if (film == null || user == null) {
            throw new ResourceNotFoundException("Фильм или пользователь не найден");
        }
        if (film.getLikes().contains(userId)) {
            Set<Integer> likes = film.getLikes();
            likes.remove(userId);
            film.setLikes(likes);
            filmStorage.updateFilm(film);
        } else {
            throw new ValidationException("Пользователь не ставил лайк этому фильму");
        }
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        List<Film> allFilms = new ArrayList<>(filmStorage.getAllFilms().values());
        System.out.println(allFilms.isEmpty());
        if (!allFilms.isEmpty()) {
            allFilms.sort(Comparator.comparingInt(film -> -film.getLikes().size()));
            if (count == null) {
                count = 10;
            }
            List<Film> topFilms = new ArrayList<>();
            for (int i = 0; i < count && i < allFilms.size(); i++) {
                topFilms.add(allFilms.get(i));
            }
            return topFilms;
        } else {
            return Collections.emptyList();
        }
    }

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(Integer id) {
        return filmStorage.getGenreById(id);
    }

    public List<MPA> getAllRatings() {
        return filmStorage.getAllRatings();
    }

    public MPA getRatingById(Integer id) {
        return filmStorage.getRatingById(id);
    }

    @Override
    public Film getFilmById(Integer filmId) {
        if (getFilmsById(filmId) == null)
            throw new InternalServerErrorException("Фильм не найден");
        return getFilmsById(filmId);
    }

    public void createFilm(Film film) {
        filmStorage.createFilm(film);
    }

    public void updateFilm(Film film) {
        filmStorage.updateFilm(film);
    }

    public Map<Integer, Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmsById(Integer filmId) {
        return filmStorage.getFilmsById(filmId);
    }

    public Boolean findById(Film film) {
            return filmStorage.findById(film);

    }


}
