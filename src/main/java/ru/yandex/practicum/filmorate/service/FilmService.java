package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@Slf4j
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Collection<Film> findPopularFilms(long count) {
        return filmStorage.findPopularFilms(count);
    }

    public Film findFilmById(long id) {
        return filmStorage.getFilmById(id);
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void putLike(long filmId, long userId) {
        userStorage.getUserById(userId);
        filmStorage.putLike(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        userStorage.getUserById(userId);
        filmStorage.removeLike(filmId, userId);
    }

}
