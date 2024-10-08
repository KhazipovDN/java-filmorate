package ru.yandex.practicum.filmorate.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;
import java.util.Set;

public interface UserStorage {
    void createUser(User user);

    void updateUser(User user);

    Map<Integer, User> getAllUsers();

    Boolean findById(User user);

    User getUserById(Integer id);

    Set<User> getUserFriends(Integer id);
}
