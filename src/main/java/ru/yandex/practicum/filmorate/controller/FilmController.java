package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;


@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAllFilms() {
        return filmService.findAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public Collection<Film> findPopularFilms(
            @RequestParam(name = "count", defaultValue = "10")
            @Positive(message = "Параметр count должен быть положительным") long count) {
        return filmService.findPopularFilms(count);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void putLike(@PathVariable long filmId, @PathVariable long userId) {
        filmService.putLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable long filmId, @PathVariable long userId) {
        filmService.removeLike(filmId, userId);
    }
}
