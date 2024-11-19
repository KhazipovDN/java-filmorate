package ru.yandex.practicum.filmorate.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    int id = 0;

    @Override
    public void createFilm(Film film) {
        id++;
        film.setId(id);
        films.put(id,film);
    }

    @Override
    public void updateFilm(Film film) {
        films.remove(id);
        film.setId(id);
        films.put(id,film);
    }

    @Override
    public Map<Integer, Film> getAllFilms() {
        return films;
    }

    @Override
    public Film getFilmsById(Integer filmId) {
        return films.get(filmId);
    }

    @Override
    public void deleteFilm(Film film) {

    }

    @Override
    public List<Genre> getAllGenres() {
        return List.of();
    }

    @Override
    public Genre getGenreById(Integer id) {
        return null;
    }

    @Override
    public List<MPA> getAllRatings() {
        return List.of();
    }

    @Override
    public MPA getRatingById(Integer id) {
        return null;
    }

    @Override
    public Boolean findById(Film film) {
        Integer id = film.getId();
        if (films.containsKey(id)) {
        return true;
        } else {
            return false;
        }
    }
}
