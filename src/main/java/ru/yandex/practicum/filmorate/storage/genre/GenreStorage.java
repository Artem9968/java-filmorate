package ru.yandex.practicum.filmorate.storage.genre;


import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;


public interface GenreStorage {

    public Collection<Genre> findAll();

    public Genre findById(Integer id);

    boolean deleteById(Integer id);
}
