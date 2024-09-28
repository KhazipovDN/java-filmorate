package ru.yandex.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.myException.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        if (!films.containsKey(id)) {
            throw new ResourceNotFoundException("Фильм не найден");
        }
        films.remove(id);
        film.setId(id);
        films.put(id,film);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
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
