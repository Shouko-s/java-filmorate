package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dal.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.MpaStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public FilmServiceImpl(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            GenreStorage genreStorage,
            MpaStorage mpaStorage,
            JdbcTemplate jdbcTemplate) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Collection<Film> findAllFilms() {
        Collection<Film> films = filmStorage.findAllFilms();
        enrichFilms(films);
        return films;
    }

    @Override
    public Film getFilmById(long id) {
        Film film = getFilmByIdOrThrow(id);
        enrichFilms(Collections.singletonList(film));
        return film;
    }

    @Override
    public Collection<Film> findPopularFilms(long count) {
        Collection<Film> films = filmStorage.findPopularFilms(count);
        enrichFilms(films);
        return films;
    }

    @Override
    public Film createFilm(Film film) {
        requireMpaExists(film.getMpa());
        validateGenresExist(film.getGenres());

        Film created = filmStorage.createFilm(film);
        enrichFilms(Collections.singletonList(created));
        return created;
    }

    @Override
    public Film updateFilm(Film film) {
        getFilmByIdOrThrow(film.getId());
        requireMpaExists(film.getMpa());
        validateGenresExist(film.getGenres());

        Film updated = filmStorage.updateFilm(film);
        enrichFilms(Collections.singletonList(updated));
        return updated;
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

    //------------------ H E L P E R -----------------

    private void enrichFilms(Collection<Film> films) {
        if (films == null || films.isEmpty()) return;

        List<Long> filmIds = films.stream().map(Film::getId).collect(Collectors.toList());

        Set<Integer> mpaIds = new HashSet<>();
        for (Film f : films) {
            if (f.getMpa() != null) {
                mpaIds.add(f.getMpa().getId());
            }
        }

        Map<Long, List<Genre>> genresByFilm = genreStorage.findByFilmIds(filmIds);
        Map<Integer, Mpa> mpaById = mpaStorage.findByIds(mpaIds);

        for (Film f : films) {
            List<Genre> genres = genresByFilm.get(f.getId());
            if (genres != null) {
                f.setGenres(new ArrayList<>(new LinkedHashSet<>(genres)));
            }

            if (f.getMpa() != null) {
                Mpa mpa = mpaById.get(f.getMpa().getId());
                if (mpa != null) {
                    f.setMpa(mpa);
                }
            }
        }
    }

    private void requireMpaExists(Mpa mpa) {
        if (mpa == null) throw new NotFoundException("MPA не задан");
        mpaStorage.findById(mpa.getId())
                .orElseThrow(() -> new NotFoundException("MPA не найден: " + mpa.getId()));
    }

    private void validateGenresExist(Collection<Genre> genres) {
        if (genres == null || genres.isEmpty()) return;
        for (Genre g : new HashSet<>(genres)) {
            genreStorage.findById(g.getId())
                    .orElseThrow(() -> new NotFoundException("Жанр не найден: " + g.getId()));
        }
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
