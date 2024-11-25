package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.user.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    @Qualifier("userDbStorage")
    private UserStorage userStorage;

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        User user = userStorage.getUserById(userId);
        Map<Integer, Boolean> userFriends = user.getFriendshipMap();
        userFriends.put(friendId, true);
        user.setFriendshipMap(userFriends);
        userStorage.updateUser(user);

        User friend = userStorage.getUserById(friendId);
        Map<Integer, Boolean> friendFriends = friend.getFriendshipMap();
        friendFriends.put(userId, true);
        friend.setFriendshipMap(friendFriends);
        userStorage.updateUser(friend);

        userStorage.updateFriends(userId,friendId);

    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (userStorage.checkFriendshipStatus(userId, friendId)) {
            Map<Integer, Boolean> userFriends = user.getFriendshipMap();
            userFriends.remove(friendId);
            user.setFriendshipMap(userFriends);
            userStorage.removeFriend(userId, friendId);

            Map<Integer, Boolean> friendFriends = friend.getFriendshipMap();
            if (friendFriends.containsKey(userId)) {
                friendFriends.remove(userId);
                friend.setFriendshipMap(friendFriends);
                userStorage.updateUser(friend);
            }
        }
    }

    @Override
    public List<User> getMutualFriends(Integer userId, Integer friendId) {
        Set<User> userFriends = userStorage.getUserFriends(userId);
        Set<User> friendFriends = userStorage.getUserFriends(friendId);
        if (Objects.nonNull(userFriends) && Objects.nonNull(friendFriends)) {
            List<User> mutualFriends = new ArrayList<>();
            for (User user : userFriends) {
                if (friendFriends.contains(user)) {
                    mutualFriends.add(user);
                }
            }
            return mutualFriends;
        }
        return new ArrayList<>();
    }

    @Override
    public User getUserById(Integer userId) {
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
