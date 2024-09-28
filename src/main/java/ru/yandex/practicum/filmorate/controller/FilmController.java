package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.film.FilmStorage;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.myException.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.myException.ValidationException;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    @Autowired
    private FilmStorage filmStorage;
    @Autowired
    private FilmService filmService;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);


    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) throws ResourceNotFoundException {
        return filmService.getFilmById(id);
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) throws ValidationException {
        log.info("Создание нового фильма", film);
        filmStorage.createFilm(film);
        log.info("Фильм создан", film);
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film updatedFilm) throws ValidationException {
        if (filmStorage.findById(updatedFilm)) {
            log.info("Обновление фильма", updatedFilm);
            filmStorage.updateFilm(updatedFilm);
            log.info("Фильм обновлен", updatedFilm);
            return new ResponseEntity<>(updatedFilm, HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("Фильм не найден");
        }
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable Integer id, @PathVariable Integer userId)  {
        filmService.likeFilm(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> unlikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.unlikeFilm(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/popular")
    public List<Film> getPopularMovies(@RequestParam(value = "count", required = false) Integer count) {
        return filmService.getTopFilms(count);
    }
}
