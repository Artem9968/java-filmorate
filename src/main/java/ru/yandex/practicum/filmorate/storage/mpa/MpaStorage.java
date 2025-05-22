package ru.yandex.practicum.filmorate.storage.mpa;


import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;


public interface MpaStorage {
    public Collection<Mpa> findAll();

    public Mpa findById(Long id);

    public boolean deleteById(Integer id);


}