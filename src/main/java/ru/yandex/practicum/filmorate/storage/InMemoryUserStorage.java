package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> friends = new HashMap<>();

    @Override
    public Collection<User> findAllUsers() {
        log.info("Получен список пользователей — {} пользователей", users.size());
        return users.values();
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User createUser(User user) {
        postProcessName(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        friends.put(user.getId(), new HashSet<>());
        log.info("Создан пользователь с id - {}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.info("Пользователь с id - {} обновлен", user.getId());
        return user;
    }

    @Override
    public Set<Long> addFriend(long id, long friendId) {
        friends.get(id).add(friendId);
        friends.get(friendId).add(id);
        log.info("Пользователь с id - {} был добавлен в друзья", friendId);
        return friends.get(id);
    }

    @Override
    public Set<User> findMyFriends(long id) {
        log.info("Получен список друзей");
        return friends.get(id).stream()
                .map(users::get)
                .collect(Collectors.toSet());
    }

    @Override
    public User deleteFriend(long id, long friendId) {
        friends.get(id).remove(friendId);
        friends.get(friendId).remove(id);
        return users.get(friendId);
    }

    @Override
    public Set<User> findCommonFriends(long id, long otherId) {
        Set<Long> otherFriends = friends.get(otherId);
        return friends.get(id).stream()
                .filter(otherFriends::contains)   // оставляем только тех, кто есть во втором множестве
                .map(users::get)                   // достаём объект User по ID
                .collect(Collectors.toSet());
    }

    private void postProcessName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя не передано, вместо имени установлен логин");
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
