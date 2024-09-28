package ru.yandex.practicum.filmorate.user;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface UserStorage {
    void createUser(User user);

    void updateUser(User user);

    List<User> getAllUsers();

    Boolean findById(User user);
}
