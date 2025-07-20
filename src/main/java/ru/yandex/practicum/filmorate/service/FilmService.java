package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {

    Collection<Film> findAllFilms();

    Collection<Film> findPopularFilms(long count);

    Film getFilmById(long id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void putLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

}
