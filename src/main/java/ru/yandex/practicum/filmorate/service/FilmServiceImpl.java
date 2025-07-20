package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    @Override
    public Collection<Film> findPopularFilms(long count) {
        if (count <= 0) {
            throw new ValidationException("count должен быть положительным");
        }
        return filmStorage.findPopularFilms(count);
    }

    @Override
    public Film getFilmById(long id) {
        return filmStorage.getFilmById(id)
                .orElseThrow(
                        () -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }

    @Override
    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());
        return filmStorage.updateFilm(film);
    }

    @Override
    public void putLike(long filmId, long userId) {
        getFilmById(filmId);
        userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        filmStorage.putLike(filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        getFilmById(filmId);
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        filmStorage.removeLike(filmId, userId);
    }
}
