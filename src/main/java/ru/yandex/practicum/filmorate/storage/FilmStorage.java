package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAllFilms();

    Collection<Film> findPopularFilms(long count);

    Optional<Film> getFilmById(long id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void putLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    void saveGenresForFilm(long filmId, List<Genre> genres);
}
