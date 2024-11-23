package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.myException.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.myException.ValidationException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FilmController {

    @Autowired
    private FilmServiceImpl filmServiceImpl;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable Integer id) throws ResourceNotFoundException {
        return filmServiceImpl.getFilmById(id);
    }

    @PostMapping("/films")
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) throws ValidationException {
        log.info("Создание нового фильма", film);
        filmServiceImpl.createFilm(film);
        log.info("Фильм создан", film);
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    @PutMapping("/films")
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film updatedFilm) throws ValidationException {
        if (filmServiceImpl.findById(updatedFilm)) {
            log.info("Обновление фильма", updatedFilm);
            filmServiceImpl.updateFilm(updatedFilm);
            log.info("Фильм обновлен", updatedFilm);
            return new ResponseEntity<>(updatedFilm, HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("Фильм не найден123");
        }
    }

    @GetMapping("/films")
    public List<Film> getAllFilms() {
        return new ArrayList<>(filmServiceImpl.getAllFilms().values());
    }

    @PutMapping("/films/{id}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable Integer id, @PathVariable Integer userId)  {
        filmServiceImpl.likeFilm(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public ResponseEntity<Void> unlikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        filmServiceImpl.unlikeFilm(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularMovies(@RequestParam(value = "count", required = false) Integer count) {
        return filmServiceImpl.getTopFilms(count);
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        return filmServiceImpl.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenresById(@PathVariable Integer id) {
        return filmServiceImpl.getGenreById(id);
    }

    @GetMapping("/mpa")
    public List<MPA> getAllRatings() {
        return filmServiceImpl.getAllRatings();
    }

    @GetMapping("/mpa/{id}")
    public MPA getRatingById(@PathVariable Integer id) {
        return filmServiceImpl.getRatingById(id);
    }


}
