package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {

    Mpa getMpaById(Long id) throws NotFoundException;

    List<Mpa> getAllMpa();
}
