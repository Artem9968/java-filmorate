package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface GenreStorage {

    public Collection<Genre> findAll();

    public Genre findById(Integer id);

    boolean deleteById(Integer id);
}
