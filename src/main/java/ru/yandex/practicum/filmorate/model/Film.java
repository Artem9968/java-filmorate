package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Set;


@Data
@Builder
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    @Positive
    private long duration;
    private Set<Genre> genres;
    private Mpa mpa;
    private Set<Long> likes;
}