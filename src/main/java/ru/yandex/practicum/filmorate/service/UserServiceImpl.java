package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.myException.ValidationException;
import ru.yandex.practicum.filmorate.myenum.Friendship;
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
        if (!user.getFriendshipMap().containsKey(friendId)) {
            if (!friend.getFriendshipMap().containsKey(userId)) {
                user.getFriendshipMap().put(friendId, Friendship.PENDING);
            } else {
                user.getFriendshipMap().put(friendId, Friendship.ACCEPTED);
                friend.getFriendshipMap().remove(userId);
                friend.getFriendshipMap().put(userId, Friendship.ACCEPTED);
            }
        } else {
            throw new ValidationException("Вы уже отправили заявку/добавили в друзья");
        }
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        User user = userStorage.getAllUsers().get(userId);
        User friend = userStorage.getAllUsers().get(friendId);
        if (user == null || friend == null) {
            throw new ResourceNotFoundException("Пользователь(и) не найден");
        }
        if (user.getFriendshipMap().containsKey(friendId)) {
            user.getFriendshipMap().remove(friendId);
            friend.getFriendshipMap().remove(userId);
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
        for (Integer mutualfriendId : user.getFriendshipMap().keySet()) {
            if (friend.getFriendshipMap().containsKey(mutualfriendId) &&
                    (friend.getFriendshipMap().get(mutualfriendId) == Friendship.ACCEPTED) &&
                    user.getFriendshipMap().get(mutualfriendId) == Friendship.ACCEPTED)
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
