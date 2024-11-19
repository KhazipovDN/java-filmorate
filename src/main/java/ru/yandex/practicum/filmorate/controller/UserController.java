package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.myException.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.myException.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class UserController {

    @Autowired
    private UserServiceImpl userServiceImpl;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Integer id) {
        return userServiceImpl.getUserById(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userServiceImpl.addFriend(id, friendId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userServiceImpl.removeFriend(id, friendId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{id}/friends")
    public Set<User> getFriends(@PathVariable Integer id) {
        if (userServiceImpl.getUserById(id) == null)
            throw new ResourceNotFoundException("Пользователь не найден");
        return userServiceImpl.getUserFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userServiceImpl.getMutualFriends(id, otherId);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) throws ValidationException {
        log.info("Создание нового пользователя", user);
        userServiceImpl.createUser(user);
        log.info("Пользователь создан", user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User updatedUser) throws ValidationException {
        if (userServiceImpl.findById(updatedUser)) {
            log.info("Обновление пользователя", updatedUser);
            System.out.println(updatedUser.toString());
            userServiceImpl.updateUser(updatedUser);
            log.info("Пользователь создан", updatedUser);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(updatedUser, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return new ArrayList<>(userServiceImpl.getAllUsers().values());
    }

}
