package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Length(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    @NotNull(message = "Дата релиза обязательна")
    private LocalDate releaseDate;

    @Positive(message = "Длительность фильма должна быть положительной")
    private int duration;

    @AssertTrue(message = "Дата релиза - не раньше 28 декабря 1895 года")
    private boolean isReleaseDateValid() {
        return releaseDate == null
                || !releaseDate.isBefore(LocalDate.of(1895, 12, 28));
    }
}
