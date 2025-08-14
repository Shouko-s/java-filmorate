package ru.yandex.practicum.filmorate.storage.dal;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dal.mappers.GenreRowMapper;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper rowMapper;

    public List<Genre> findAll() {
        String sql = "SELECT id, name FROM genres ORDER BY id";
        return jdbc.query(sql, rowMapper);
    }

    public Optional<Genre> findById(int id) {
        String sql = "SELECT id, name FROM genres WHERE id = ?";
        try {
            Genre g = jdbc.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(g);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
