package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data

public class Film {

    private Long id;

    @NotBlank(message = "Название фильма не должно быть пустым")
    private String name;

    @Size(max = 200, message = "Описание фильма не более 200 символов")
    private String description;

    @NotNull(message = "Дата релиза должна присутствовать")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность фильма необходимо указать")
    private int duration;

    private Set<Long> likedUsers = new HashSet<>();
}