package ru.yandex.practicum.filmorate.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.baserepository.BaseRepository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.myException.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.myenum.Friendship;

import java.util.stream.Collectors;

import java.util.*;

import static ru.yandex.practicum.filmorate.myenum.Friendship.ACCEPTED;
import static ru.yandex.practicum.filmorate.myenum.Friendship.PENDING;

@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private static final String create = "INSERT INTO users (id, login, name, email, birthday) VALUES (?, ?, ?, ?, ?)";
    private static final String getall = "SELECT * FROM users";
    private static final String getuser = "SELECT * FROM users WHERE id = ?";
    private static final String findid = "SELECT count(*) FROM users WHERE id = ?";
    private static final String getuserfriends = "SELECT u.* FROM users u JOIN friendship f ON u.id = f.friendId WHERE f.userId = ? AND f.status = 'ACCEPTED'";

    @Autowired
    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    int id;

    private Integer countColomn() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        if (count == null)
            throw new ResourceNotFoundException("Ошибка подключения");
        return count;
    }

    @Override
    public void createUser(User user) {
        user.setName(checkName(user));
        id = countColomn();
        jdbc.update(create, id, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());
    }

    @Override
    public void updateUser(User user) {
        String checkUser = "SELECT COUNT(*) FROM users WHERE id = ?";
        String update = "UPDATE users SET login = ?, name = ?, email = ?, birthday = ? WHERE id = ?";
        System.out.println(user.getId());
        Integer count = jdbc.queryForObject(checkUser, Integer.class, user.getId());
        if (count == null || count == 0) {
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        int rowsUpdated = jdbc.update(update, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId());
        if (rowsUpdated == 0) {
            throw new ResourceNotFoundException("Не удалось обновить данные");
        }
    }

    @Override
    public Map<Integer, User> getAllUsers() {
        List<User> users = jdbc.query(getall, mapper);
        return users.stream().collect(Collectors.toMap(User::getId, user -> user));
    }

    @Override
    public User getUserById(Integer id) {
        return jdbc.queryForObject(getuser, mapper, id);
    }

    @Override
    public Boolean findById(User user) {
        Integer count = jdbc.queryForObject(findid, Integer.class, user.getId());
        return count != null && count > 0;
    }

    @Override
    public Set<User> getUserFriends(Integer id) {
        List<User> friends = jdbc.query(getuserfriends, mapper, id);
        return new HashSet<>(friends);
    }

    @Override
    public void createFriendship(Integer userId, Integer friendId) {
        String insert = "INSERT INTO friendship (userId, friendId, status) VALUES (?, ?, ?)";
        jdbc.update(insert, userId, friendId, Friendship.PENDING.name());
    }

    @Override
    public void updateFriendship(Integer userId, Integer friendId) {
        String update = "UPDATE friendship SET status = ? WHERE userId = ? AND friendId = ?";
        jdbc.update(update, Friendship.ACCEPTED.name(), userId, friendId);

    }
    @Override
    public int checkFriendship(Integer userId, Integer friendId) {
        String checkFriendship = "SELECT COUNT(*) FROM friendship WHERE (userId = ? AND friendId = ?)";
        Integer count = jdbc.queryForObject(checkFriendship, Integer.class, userId, friendId);
        if (count != null && count > 0) {
            return 1;
        }
        count = jdbc.queryForObject(checkFriendship, Integer.class, friendId, userId);
        if (count != null && count > 0) {
            String getStatus = "SELECT status FROM friendship WHERE (userId = ? AND friendId = ?)";
            Friendship friendship = Friendship.valueOf(jdbc.queryForObject(getStatus, String.class, friendId, userId));
            if (friendship == PENDING) {
                return 2;
            } else
                return 3;
        }
        return 0;
    }


    private String checkName(User user) {
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            return user.getLogin();
        } else {
            return user.getName();
        }
    }

    private void deleteLikes(User user) {
        String sqlQuery = "DELETE FROM likes WHERE userId = ?";
        jdbc.update(sqlQuery, user.getId());
    }

    private void deleteFriendship(User user) {
        String sqlQuery = "DELETE FROM friendship WHERE userId = ? OR friendId = ?";
        jdbc.update(sqlQuery, user.getId(), user.getId());
    }

    private void deleteFromUsers(User user) {
        String sqlQuery = "DELETE FROM users WHERE userId = ?";
        jdbc.update(sqlQuery, user.getId());
    }

    @Override
    public void deleteFriendship(Integer userId, Integer friendId) {
        String sql = "DELETE FROM friendship WHERE (userId = ? AND friendId = ?) OR (userId = ? AND friendId = ?)";
        jdbc.update(sql, userId, friendId, friendId, userId);
    }

    @Override
    public Boolean checkFriendshipStatus(Integer userId, Integer friendId) {
        String getStatus = "SELECT status FROM friendship WHERE (userId = ? AND friendId = ?) OR (userId = ? AND friendId = ?)";
        Friendship friendship = Friendship.valueOf(jdbc.queryForObject(getStatus, String.class, friendId, userId, friendId, userId));
        if (friendship == ACCEPTED )
            return true;
        return false;
    }

}
