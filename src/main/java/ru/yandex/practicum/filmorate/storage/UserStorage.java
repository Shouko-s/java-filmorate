package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    Collection<User> findAllUsers();

    Optional<User> getUserById(long id);

    User createUser(User user);

    User updateUser(User user);

    Set<Long> addFriend(long id, long friendId);

    Set<User> findMyFriends(long id);

    User deleteFriend(long id, long friendId);

    Set<User> findCommonFriends(long id, long otherId);
}
