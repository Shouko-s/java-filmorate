package ru.yandex.practicum.filmorate.storage.dal;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dal.mappers.MpaRowMapper;

import java.util.*;

@Repository
@AllArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbc;
    private final MpaRowMapper mpaRowMapper;

    public List<Mpa> findAll() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY id";
        return jdbc.query(sql, mpaRowMapper);
    }

    public Optional<Mpa> findById(int id) {
        String sql = "SELECT id, name FROM mpa_ratings WHERE id = ?";
        try {
            Mpa m = jdbc.queryForObject(sql, mpaRowMapper, id);
            return Optional.ofNullable(m);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Map<Integer, Mpa> findByIds(Collection<Integer> ids) {
        Map<Integer, Mpa> result = new HashMap<>();
        if (ids == null || ids.isEmpty()) return result;

        String in = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = "SELECT * FROM mpa_ratings WHERE id IN (" + in + ")";
        List<Mpa> list = jdbc.query(sql, ids.toArray(), mpaRowMapper);
        for (Mpa mpa : list) {
            result.put(mpa.getId(), mpa);
        }
        return result;
    }
}
