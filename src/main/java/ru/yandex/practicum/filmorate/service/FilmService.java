package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {

    Collection<Film> findAllFilms();

    Film getFilmById(long id);

    Collection<Film> findPopularFilms(long count);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void putLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

}
