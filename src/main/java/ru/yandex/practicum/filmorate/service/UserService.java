package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.myException.InternalServerErrorException;
import ru.yandex.practicum.filmorate.user.UserStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.myException.ResourceNotFoundException;

import java.util.*;

@Service
public class UserService implements UserServiceInterface {

    @Autowired
    private UserStorage userStorage;

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        User user = userStorage.getAllUsers().get(userId);
        User friend = userStorage.getAllUsers().get(friendId);
        if (user == null || friend == null) {
            throw new InternalServerErrorException("Пользователь(и) не найден");
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        User user = userStorage.getAllUsers().get(userId);
        User friend = userStorage.getAllUsers().get(friendId);
        if (user == null || friend == null) {
            throw new InternalServerErrorException("Пользователь(и) не найден");
        }
        if (user.getFriends().contains(friendId)) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
        }
    }

    @Override
    public List<Integer> getMutualFriends(Integer userId, Integer friendId) {
        List<Integer> mutualFriends = new ArrayList<>();
        User user = userStorage.getAllUsers().get(userId);
        User friend = userStorage.getAllUsers().get(friendId);
        if (user == null || friend == null) {
            throw new InternalServerErrorException("Пользователь(и) не найден");
        }
        for (Integer mutualfriendId : user.getFriends()) {
            if (friend.getFriends().contains(mutualfriendId))
                mutualFriends.add(mutualfriendId);
        }
        return mutualFriends;
    }

    @Override
    public User getUserById(Integer userId) {
        if (!userStorage.getAllUsers().containsKey(userId))
            throw new ResourceNotFoundException("Пользователь не найден");
        return userStorage.getAllUsers().get(userId);
    }
}
