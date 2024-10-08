package ru.yandex.practicum.filmorate.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.myException.ResourceNotFoundException;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    int id = 0;

    @Override
    public void createUser(User user) {
        id++;
        user.setId(id);
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(id,user);
    }

    @Override
    public void updateUser(User user) {
        Integer id = user.getId();
        if (users.get(id) == null) {
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        users.remove(id);
        user.setId(id);
        users.put(id, user);
    }

    @Override
    public Map<Integer, User> getAllUsers() {
        return users;
    }

    @Override
    public User getUserById(Integer id) {
        return users.get(id);
    }

    @Override
    public Boolean findById(User user) {
        Integer id = user.getId();
        if (users.containsKey(id)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Set<User> getUserFriends(Integer id) {
        Set<User> friends = new HashSet<>();
        for (Integer friendId : getUserById(id).getFriends()) {
            friends.add(getUserById(friendId));
        }
        return friends;
    }
}
