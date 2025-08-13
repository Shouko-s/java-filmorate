package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@Qualifier("userDbStorage")
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper userRowMapper;

    @Override
    public Collection<User> findAllUsers() {
        String sql = "SELECT id, email, login, name, birthday FROM users";
        return jdbc.query(sql, userRowMapper);
    }

    @Override
    public Optional<User> getUserById(long id) {
        String sql = "SELECT id, email, login, name, birthday FROM users WHERE id = ?";
        try {
            User user = jdbc.queryForObject(sql, userRowMapper, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbc.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public Set<Long> addFriend(long id, long friendId) {
        String sql = "INSERT INTO friendship (user_id, friend_id, status) VALUES (?, ?, 'UNCONFIRMED')";
        jdbc.update(sql, id, friendId);

        return getFriendIds(id);
    }

    private Set<Long> getFriendIds(long userId) {
        String sql = "SELECT friend_id FROM friendship WHERE user_id = ?";
        return new HashSet<>(jdbc.queryForList(sql, Long.class, userId));
    }

    @Override
    public Set<User> findMyFriends(long id) {
        String sql = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM users u " +
                "JOIN friendship f ON u.id = f.friend_id " +
                "WHERE f.user_id = ?";
        return new HashSet<>(jdbc.query(sql, userRowMapper, id));
    }

    @Override
    public User deleteFriend(long id, long friendId) {
        String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbc.update(sql, id, friendId);
        return getUserById(id).orElse(null);
    }

    @Override
    public Set<User> findCommonFriends(long id, long otherId) {
        String sql = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM users u " +
                "JOIN friendship f1 ON u.id = f1.friend_id " +
                "JOIN friendship f2 ON u.id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return new HashSet<>(jdbc.query(sql, userRowMapper, id, otherId));
    }

//    Извините, не успел закончить ФЗ.
//    Работу отправил на проверку до дедлайна.
//    Насколько знаю, в таком случае можно дорабатывать и пересдавать после дедлайна,
//    пока не сдам. Днём доделаю и пришлю полную версию.
}
