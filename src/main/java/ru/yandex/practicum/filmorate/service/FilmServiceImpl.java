package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmServiceImpl(
            FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    @Override
    public Film getFilmById(long id) {
        return getFilmByIdOrThrow(id);
    }

    @Override
    public Collection<Film> findPopularFilms(long count) {
        return filmStorage.findPopularFilms(count);
    }

    @Override
    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        getFilmByIdOrThrow(film.getId());
        return filmStorage.updateFilm(film);
    }

    @Override
    public void putLike(long filmId, long userId) {
        getFilmByIdOrThrow(filmId);
        getUserByIdOrThrow(userId);
        filmStorage.putLike(filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        getFilmByIdOrThrow(filmId);
        getUserByIdOrThrow(userId);
        filmStorage.removeLike(filmId, userId);
    }

    private Film getFilmByIdOrThrow(long id) {
        return filmStorage.getFilmById(id)
                .orElseThrow(
                        () -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }

    private User getUserByIdOrThrow(long id) {
        return userStorage.getUserById(id)
                .orElseThrow(
                        () -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }
}
