package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
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
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Integer, User> users = new HashMap<>();
    int id = 0;

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) throws ValidationException {
        id++;
        log.info("Создание нового пользователя", user);
        user.setId(id);
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(id,user);
        log.info("Пользователь создан", user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User updatedUser) throws ValidationException {
        Integer id = updatedUser.getId();
        if (id == null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.BAD_REQUEST);
        }
        if (users.containsKey(id)) {
            log.info("Обновление нового пользователя", updatedUser);
            users.remove(id);
            updatedUser.setId(id);
            users.put(id, updatedUser);
            log.info("Пользователь создан", updatedUser);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(updatedUser, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

}
