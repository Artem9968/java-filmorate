package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor(staticName = "of")
public class Genre {
    private Long id;
    private String name;
}
