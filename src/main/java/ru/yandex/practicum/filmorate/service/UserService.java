package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public Set<Long> addFriend(long id, long friendId) {
        return userStorage.addFriend(id, friendId);
    }

    public Set<User> findMyFriends(long id) {
        return userStorage.findMyFriends(id);
    }

    public User deleteFriend(long id, long friendId) {
        return userStorage.deleteFriend(id, friendId);
    }

    public Set<User> findCommonFriends(long id, long friendId) {
        return userStorage.findCommonFriends(id, friendId);
    }

}
