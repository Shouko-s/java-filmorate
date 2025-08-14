package ru.yandex.practicum.filmorate.storage.dal;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmRowMapper;

import java.sql.Statement;
import java.util.*;

@Repository
@Qualifier("filmDbStorage")
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmRowMapper;

    @Override
    public Collection<Film> findAllFilms() {
        String query = "SELECT * FROM films";
        Collection<Film> films = jdbc.query(query, filmRowMapper);

        for (Film film : films) {
            completeFilmData(film);
        }

        return films;
    }

    @Override
    public Collection<Film> findPopularFilms(long count) {
        String query = "SELECT f.* FROM films AS f " + "LEFT JOIN film_likes AS fl ON f.id = fl.film_id " + "GROUP BY f.id " + "ORDER BY COUNT(fl.user_id) DESC " + "LIMIT ?";
        Collection<Film> films = jdbc.query(query, filmRowMapper, count);

        for (Film film : films) {
            completeFilmData(film);
        }

        return films;
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        String query = "SELECT * FROM films WHERE id = ?";
        try {
            Film film = jdbc.queryForObject(query, filmRowMapper, id);
            if (film != null) {
                completeFilmData(film);
            }
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Film createFilm(Film film) {
        requireMpaExists(film.getMpa().getId());

        if (film.getGenres() != null) {
            for (Genre g : film.getGenres()) {
                requireGenreExists(g.getId());
            }
        }

        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) " + "VALUES (?, ?, ?, ?, ?)";
        KeyHolder kh = new GeneratedKeyHolder();

        jdbc.update(conn -> {
            var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, kh);

        film.setId(Objects.requireNonNull(kh.getKey()).longValue());

        saveGenresForFilm(film.getId(), film.getGenres());

        completeFilmData(film);
        return film;
    }


    @Override
    public Film updateFilm(Film film) {
        requireFilmExists(film.getId());
        requireMpaExists(film.getMpa().getId());
        if (film.getGenres() != null) {
            for (Genre g : film.getGenres()) requireGenreExists(g.getId());
        }

        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? " + "WHERE id = ?";
        jdbc.update(sql, film.getName(), film.getDescription(), java.sql.Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getMpa().getId(), film.getId());

        saveGenresForFilm(film.getId(), film.getGenres());
        completeFilmData(film);
        return film;
    }


    @Override
    public void putLike(long filmId, long userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        try {
            jdbc.update(sql, filmId, userId);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось поставить лайк", e);
        }
    }

    @Override
    public void removeLike(long filmId, long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbc.update(sql, filmId, userId);
    }

    private Mpa getMpaByFilmId(long filmId) {
        String sql = "SELECT r.id, r.name " + "FROM mpa_ratings r JOIN films f ON r.id = f.mpa_rating_id " + "WHERE f.id = ?";
        return jdbc.queryForObject(sql, (rs, rn) -> {
            Mpa m = new Mpa();
            m.setId(rs.getInt("id"));
            m.setName(rs.getString("name"));
            return m;
        }, filmId);
    }

    private List<Genre> getGenresForFilm(long filmId) {
        String sql = "SELECT g.id, g.name " + "FROM genres g JOIN film_genres fg ON g.id = fg.genre_id " + "WHERE fg.film_id = ? " + "ORDER BY g.id";
        return jdbc.query(sql, (rs, rn) -> {
            Genre g = new Genre();
            g.setId(rs.getInt("id"));
            g.setName(rs.getString("name"));
            return g;
        }, filmId);
    }

    private void saveGenresForFilm(long filmId, List<Genre> genres) {
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
        if (genres == null || genres.isEmpty()) return;

        String insert = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        // Можно убрать дубли на всякий случай
        genres.stream().map(Genre::getId).distinct().forEach(genreId -> jdbc.update(insert, filmId, genreId));
    }

    private void completeFilmData(Film film) {
        film.setMpa(getMpaByFilmId(film.getId()));
        film.setGenres(getGenresForFilm(film.getId()));
    }

    private void requireMpaExists(int mpaId) {
        Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM mpa_ratings WHERE id = ?", Integer.class, mpaId);
        if (cnt == 0) throw new NotFoundException("MPA не найден: " + mpaId);
    }

    private void requireGenreExists(int genreId) {
        Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM genres WHERE id = ?", Integer.class, genreId);
        if (cnt == 0) throw new NotFoundException("Жанр не найден: " + genreId);
    }

    private void requireFilmExists(long id) {
        Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM films WHERE id = ?", Integer.class, id);
        if (cnt == 0) throw new NotFoundException("Фильм не найден: " + id);
    }
}
