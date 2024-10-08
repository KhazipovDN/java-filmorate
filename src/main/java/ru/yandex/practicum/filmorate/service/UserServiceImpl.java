package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.user.UserStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.myException.ResourceNotFoundException;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserStorage userStorage;

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        User user = userStorage.getAllUsers().get(userId);
        User friend = userStorage.getAllUsers().get(friendId);
        if (user == null || friend == null) {
            throw new ResourceNotFoundException("Пользователь(и) не найден(ы)");
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        User user = userStorage.getAllUsers().get(userId);
        User friend = userStorage.getAllUsers().get(friendId);
        if (user == null || friend == null) {
            throw new ResourceNotFoundException("Пользователь(и) не найден");
        }
        if (user.getFriends().contains(friendId)) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
        }
    }

    @Override
    public List<User> getMutualFriends(Integer userId, Integer friendId) {
        List<User> mutualFriends = new ArrayList<>();
        User user = userStorage.getAllUsers().get(userId);
        User friend = userStorage.getAllUsers().get(friendId);
        if (user == null || friend == null) {
            throw new ResourceNotFoundException("Пользователь(и) не найден");
        }
        for (Integer mutualfriendId : user.getFriends()) {
            if (friend.getFriends().contains(mutualfriendId))
                mutualFriends.add(getUserById(mutualfriendId));
        }
        return mutualFriends;
    }

    @Override
    public User getUserById(Integer userId) {
        if (userStorage.getUserById(userId) == null)
            throw new ResourceNotFoundException("Пользователь не найден");
        return userStorage.getUserById(userId);
    }

    public void createUser(User user) {
        userStorage.createUser(user);
    }

    public void updateUser(User user) {
        userStorage.updateUser(user);
    }

    public Map<Integer, User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public Boolean findById(User user) {
        return userStorage.findById(user);
    }

    public Set<User> getUserFriends(Integer id) {
        return userStorage.getUserFriends(id);
    }

}
