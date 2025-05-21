package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    Genre getGenreById(Long id) throws NotFoundException;

    List<Genre> getAllGenres();
}
