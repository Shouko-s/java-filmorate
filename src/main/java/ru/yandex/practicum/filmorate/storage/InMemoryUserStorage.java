package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
        if (user.getId() == null) {
            log.warn("id пользователя не передан в запросе");
            throw new ValidationException("id пользователя должен быть указан");
        }

        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь с id - {} не найден", user.getId());
            throw new NotFoundException("Пользователь с таким id не найден");
        }

        users.put(user.getId(), user);
        log.info("Пользователь с id - {} обновлен", user.getId());
        return user;
    }

    @Override
    public Set<Long> addFriend(long id, long friendId) {
        if (!users.containsKey(id)) {
            log.warn("Пользователь с id - {} не найден", id);
            throw new NotFoundException("Пользователь с id - " + id + " не найден");
        }
        if (!users.containsKey(friendId)) {
            log.warn("Пользователь с id - {} не найден (друг)", friendId);
            throw new NotFoundException("Пользователь с id - " + friendId + " не найден");
        }

        if (friends.get(id).contains(friendId)) {
            log.warn("Пользователь с id - {} уже в друзьях", friendId);
            throw new RuntimeException("Пользователь с id - " + friendId + " уже в друзьях");
        }

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
