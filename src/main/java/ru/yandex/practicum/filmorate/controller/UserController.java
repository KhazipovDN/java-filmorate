package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.myException.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Integer, User> users = new HashMap<>();
    int id = 0;

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) throws ValidationException {
        id++;
        log.info("Создание нового пользователя", user);
        user.setId(id);
        users.put(id,user);
        log.info("Фильм создан", user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User updatedUser) throws ValidationException {
        if (users.containsKey(id)) {
            log.info("Обновление нового пользователя", updatedUser);
            users.remove(id);
            users.put(id, updatedUser);
            log.info("Пользователь создан", updatedUser);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(updatedUser, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

}
