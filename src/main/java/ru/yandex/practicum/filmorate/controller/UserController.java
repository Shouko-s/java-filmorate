package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAllUsers() {
        log.info("Получен список пользователей — " + users.size() + " пользователей");
        return users.values();

    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        postProcessName(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан пользователь с id - " + user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.warn("Пользователь с id айди не найден");
            throw new ValidationException("Пользователь с таким id не найден");
        }
        users.put(user.getId(), user);
        log.info("Пользователь с id - " + user.getId() + " обновлен");
        return user;
    }

    private void postProcessName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
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
