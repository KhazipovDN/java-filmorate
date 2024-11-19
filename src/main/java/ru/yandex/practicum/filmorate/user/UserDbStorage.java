package ru.yandex.practicum.filmorate.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.myException.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.*;


@Repository
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    int id;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void createUser(User user) {
        user.setName(checkAndReturnName(user));
        String create = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(create, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        log.info("Создан пользователь с идентефикатором {}", id);
    }

    @Override
    public void updateUser(User user) {
        SqlRowSet userRS = jdbcTemplate.queryForRowSet("SELECT * FROM USERS WHERE USER_ID = ?", user.getId());
        if (userRS.next()) {
            String update = "UPDATE USERS set EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? where USER_ID = ?";
            jdbcTemplate.update(update, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
            updateFriends(user);
            log.info("Изменён пользователь с идентефикатором {}", user.getId());
        } else throw new ResourceNotFoundException("Нет пользователя с таким id");
    }

    @Override
    public void delete(User user) {
        deleteFromFilmUsersLikes(user);
        deleteFromFriends(user);
        deleteFromUsers(user);
        log.info("Удалён пользователь с идентефикатором {}", user.getId());
    }

    @Override
    public User getUserById(Integer id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM USERS WHERE USER_ID = ?", id);
        if (userRows.next()) {
            User user = makeUser(userRows);
            log.info("Найден пользователь: {} {}", user.getId(), user.getName());
            return user;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new ResourceNotFoundException("Нет пользователя с таким id");
        }
    }

    @Override
    public Map<Integer, User> getAllUsers() {
        Map<Integer, User> users = new HashMap<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM USERS");
        while (userRows.next()) {
            User user = makeUser(userRows);
            log.info("Найден пользователь: {} {}", user.getId(), user.getName());
            users.put(user.getId(), user);
        }
        return users;
    }

    @Override
    public Boolean findById(User user) {
        String findid = "SELECT count(*) FROM USERS WHERE USER_ID = ?";
        Integer count = jdbcTemplate.queryForObject(findid, Integer.class, user.getId());
        return count != null && count > 0;
    }

    @Override
    public Set<User> getUserFriends(Integer id) {
        Set<User> users = new HashSet<>();
        String getUserFriendsQuery = "SELECT u.* FROM USERS u JOIN FRIENDS f ON (u.ID = f.FRIEND_1 AND f.FRIEND_2 = ?)"+
                "OR (u.ID = f.FRIEND_2 AND f.FRIEND_1 = ?) WHERE f.CONFIRMATION = TRUE" ;
        SqlRowSet friends = jdbcTemplate.queryForRowSet(getUserFriendsQuery, id, id);
        while (friends.next()) {
            User user = makeUser(friends);
            log.info("Найден пользователь: {} {}", user.getId(), user.getName());
            users.add(user);
        }
        return users;
    }

    @Override
    public Boolean checkFriendshipStatus(Integer userId, Integer friendId) {
        return null;
    }

    private User makeUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("USER_ID"));
        user.setEmail(rs.getString("EMAIL"));
        user.setLogin(rs.getString("LOGIN"));
        user.setName(rs.getString("NAME"));
        user.setBirthday(Objects.requireNonNull(rs.getDate("BIRTHDAY")).toLocalDate());
        user.setFriendshipMap(getFriends(user.getId()));
        return user;
    }

    private void updateFriends(User user) {
        deleteFromFriends(user);
        String sqlQuery = "INSERT INTO FRIENDS (FRIEND_1, FRIEND_2, CONFIRMATION) VALUES (?, ?, ?)";
        for (Map.Entry<Integer, Boolean> entry : user.getFriendshipMap().entrySet()) {
            jdbcTemplate.update(sqlQuery, user.getId(), entry.getKey(), entry.getValue());
        }
    }

    private Map<Integer, Boolean> getFriends(Integer id) {
        SqlRowSet friendsRows1 = jdbcTemplate.queryForRowSet("SELECT FRIEND_2, CONFIRMATION FROM FRIENDS " +
                "WHERE FRIEND_1 = ?", id);
        HashMap<Integer, Boolean> friends = new HashMap<>(getMapOfFriends(friendsRows1));
        return friends;
    }

    private Map<Integer, Boolean> getMapOfFriends(SqlRowSet rs) {
        HashMap<Integer, Boolean> friends = new HashMap<>();
        while (rs.next()) {
            Integer userId = rs.getInt(1);
            Boolean confirmation = rs.getBoolean(2);
            friends.put(userId, confirmation);
        }
        return friends;
    }

    private void deleteFromUsers(User user) {
        String sqlQuery = "DELETE FROM USERS WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery, user.getId());
    }

    private void deleteFromFriends(User user) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE FRIEND_1 = ? OR FRIEND_2 = ?";
        jdbcTemplate.update(sqlQuery, user.getId(), user.getId());
    }

    private void deleteFromFilmUsersLikes(User user) {
        String sqlQuery = "DELETE FROM FILM_USERS_LIKES WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery, user.getId());
    }

    private String checkAndReturnName(User user) {
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            return user.getLogin();
        } else {
            return user.getName();
        }
    }

}