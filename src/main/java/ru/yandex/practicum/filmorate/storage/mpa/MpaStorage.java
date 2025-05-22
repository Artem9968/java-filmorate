package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;

public interface MpaStorage {
    public Collection<Mpa> findAll();

    public Mpa findById(Integer id);

    public boolean deleteById(Integer id);


}