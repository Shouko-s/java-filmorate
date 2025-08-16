package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

public interface GenreStorage {
    List<Genre> findAll();

    Optional<Genre> findById(int id);

    Map<Long, List<Genre>> findByFilmIds(Collection<Long> filmIds);
}
