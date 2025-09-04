package ru.yandex.practicum.filmorate.storage.dal;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmRowMapper;

import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@Qualifier("filmDbStorage")
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmRowMapper;

    @Override
    public Collection<Film> findAllFilms() {
        return jdbc.query("SELECT * FROM films", filmRowMapper);
    }

    @Override
    public Collection<Film> findPopularFilms(long count) {
        String sql = "SELECT f.* FROM films AS f LEFT JOIN film_likes AS fl ON f.id = fl.film_id GROUP BY f.id ORDER BY COUNT(fl.user_id) DESC LIMIT ?";
        return jdbc.query(sql, filmRowMapper, count);
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        try {
            Film film = jdbc.queryForObject(sql, filmRowMapper, id);
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Film createFilm(Film film) {
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

        return film;
    }


    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? " + "WHERE id = ?";
        jdbc.update(sql,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        saveGenresForFilm(film.getId(), film.getGenres());
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

    public void saveGenresForFilm(long filmId, List<Genre> genres) {
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
        if (genres == null || genres.isEmpty()) return;

        String insert = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        genres.stream().map(Genre::getId).distinct().forEach(genreId -> jdbc.update(insert, filmId, genreId));
    }
}
