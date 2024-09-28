package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserServiceInterface {
    void addFriend(Integer userId, Integer friendId);

    void removeFriend(Integer userId, Integer friendId);

    List<Integer> getMutualFriends(Integer userId, Integer friendId);

    User getUserById(Integer userId);
}
