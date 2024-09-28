package ru.yandex.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmStorage {
    void createFilm(Film film);
    void updateFilm(Film film);
    public List<Film> getAllFilms();
    Boolean findById (Film film);
}
