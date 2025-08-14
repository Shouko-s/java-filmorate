package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dal.MpaDbStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaDbStorage mpaDbStorage;

    @Override
    public List<Mpa> findAll() {
        return mpaDbStorage.findAll();
    }

    @Override
    public Mpa findById(int id) {
        return mpaDbStorage.findById(id).orElseThrow(() -> new NotFoundException("MPA с id=" + id + " не найден"));
    }
}
