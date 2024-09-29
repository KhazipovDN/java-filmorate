package ru.yandex.practicum.filmorate.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

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
    public Boolean findById(Film film) {
        Integer id = film.getId();
        if (films.containsKey(id)) {
        return true;
        } else {
            return false;
        }
    }
}
