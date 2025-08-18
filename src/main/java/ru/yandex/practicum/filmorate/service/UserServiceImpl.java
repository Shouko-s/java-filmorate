package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyFriendException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public UserServiceImpl(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    @Override
    public User getUserById(long id) {
        return getUserByIdOrThrow(id);
    }

    @Override
    public User createUser(User user) {
        postProcessName(user);
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        getUserByIdOrThrow(user.getId());
        postProcessName(user);
        return userStorage.updateUser(user);
    }

    @Override
    public Set<Long> addFriend(long id, long friendId) {
        getUserByIdOrThrow(id);
        User friend = getUserByIdOrThrow(friendId);
        Set<User> current = userStorage.findMyFriends(id);
        if (current.contains(friend)) {
            throw new AlreadyFriendException("Пользователь с id=" + friendId + " уже в друзьях");
        }
        return userStorage.addFriend(id, friendId);
    }

    @Override
    public Set<User> findMyFriends(long id) {
        getUserByIdOrThrow(id);
        return userStorage.findMyFriends(id);
    }

    @Override
    public User deleteFriend(long id, long friendId) {
        getUserByIdOrThrow(id);
        getUserByIdOrThrow(friendId);
        return userStorage.deleteFriend(id, friendId);
    }

    @Override
    public Set<User> findCommonFriends(long id, long otherId) {
        getUserByIdOrThrow(id);
        getUserByIdOrThrow(otherId);
        return userStorage.findCommonFriends(id, otherId);
    }

    private User getUserByIdOrThrow(long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    private void postProcessName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
