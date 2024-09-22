package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.myException.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Integer, Film> films = new HashMap<>();
    int id = 0;

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) throws ValidationException {
        log.info("Создание нового фильма", film);
        id++;
        film.setId(id);
        films.put(id,film);
        log.info("Фильм создан", film);
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film updatedFilm) throws ValidationException {
       Integer id = updatedFilm.getId();

        if (id == null) {
            return new ResponseEntity<>(updatedFilm, HttpStatus.BAD_REQUEST);
        }
        if (films.containsKey(id)) {
            log.info("Обновление фильма", updatedFilm);
            films.remove(id);
            updatedFilm.setId(id);
            films.put(id,updatedFilm);
            log.info("Фильм обновлен", updatedFilm);
            return new ResponseEntity<>(updatedFilm, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(updatedFilm, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }
}
