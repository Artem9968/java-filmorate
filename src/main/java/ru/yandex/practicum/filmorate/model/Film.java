package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor(staticName = "of")
public class Film {
    private Long id;

    @NotNull(message = "Название не может быть null")
    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 100, message = "Название не может быть длиннее 100 символов")
    private String name;

    @Description("New film update description")
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull(message = "Дата релиза не может быть null")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Дата релиза должна быть не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность не может быть null")
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;

    @JsonIgnore
    private Set<Long> likedUsers;

    @NotNull(message = "Рейтинг MPA не может быть null")
    @Min(value = 1, message = "Рейтинг MPA должен быть не меньше 1")
    @Max(value = 5, message = "Рейтинг MPA должен быть не больше 5")
    private Long mpa;

    private LinkedHashSet<Long> genres;
}