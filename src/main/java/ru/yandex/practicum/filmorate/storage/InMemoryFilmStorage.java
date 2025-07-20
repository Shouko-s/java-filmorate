package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> likes = new HashMap<>();

    @Override
    public Collection<Film> findAllFilms() {
        log.info("Получен список фильмов — {} шт.", films.size());
        return films.values();
    }

    @Override
    public Collection<Film> findPopularFilms(long count) {
        return films.values().stream()
                .sorted(Comparator
                        .comparingInt((Film f) ->
                                likes.getOrDefault(f.getId(), Collections.emptySet())
                                        .size())
                        .reversed())
                .limit(count)
                .collect(Collectors.toList());

    }

    @Override
    public Optional<Film> getFilmById(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());
        log.info("Создан фильм с id={}", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            log.warn("id фильма не передан");
            throw new ValidationException("id фильма должен быть указан");
        }
        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с id={} не найден", film.getId());
            throw new NotFoundException("Фильм с таким id не найден");
        }
        films.put(film.getId(), film);
        log.info("Фильм с id={} обновлён", film.getId());
        return film;
    }

    @Override
    public void putLike(long filmId, long userId) {
        if (!films.containsKey(filmId)) {
            log.warn("Фильм с id={} не был найден", filmId);
            throw new NotFoundException("Фильм с id=" + filmId + " не был найден");
        }

        log.info("Лайк поставлен пользователем с id={}", userId);
        likes.get(filmId).add(userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        if (!films.containsKey(filmId)) {
            log.warn("Фильм с id={} не был найден", filmId);
            throw new NotFoundException("Фильм с id=" + filmId + " не был найден");
        }

        log.info("Пользователь с id={} убрал лайк", userId);
        likes.get(filmId).remove(userId);
    }

    private long getNextId() {
        return films.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L) + 1;
    }
}
