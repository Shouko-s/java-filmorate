package ru.yandex.practicum.filmorate.storage.dal;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dal.mappers.GenreRowMapper;

import java.util.*;

@Repository
@AllArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper genreRowMapper;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Genre> findAll() {
        String sql = "SELECT id, name FROM genres ORDER BY id";
        return jdbc.query(sql, genreRowMapper);
    }

    public Optional<Genre> findById(int id) {
        String sql = "SELECT id, name FROM genres WHERE id = ?";
        try {
            Genre g = jdbc.queryForObject(sql, genreRowMapper, id);
            return Optional.ofNullable(g);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Map<Long, List<Genre>> findByFilmIds(Collection<Long> filmIds) {
        Map<Long, List<Genre>> result = new HashMap<>();
        if (filmIds == null || filmIds.isEmpty()) return result;

        String in = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String sql = "SELECT fg.film_id, g.* " +
                "FROM film_genres fg " +
                "JOIN genres g ON g.id = fg.genre_id " +
                "WHERE fg.film_id IN (" + in + ") " +
                "ORDER BY g.id";

        List<Object> params = new ArrayList<>(filmIds);
        jdbc.query(sql, params.toArray(), rs -> {
            long filmId = rs.getLong("film_id");
            Genre genre = genreRowMapper.mapRow(rs, 0);
            result.computeIfAbsent(filmId, k -> new ArrayList<>()).add(genre);
        });
        return result;
    }


}
