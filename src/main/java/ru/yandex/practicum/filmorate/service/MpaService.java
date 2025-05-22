package ru.yandex.practicum.filmorate.service;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;

import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;


@Service
@RequiredArgsConstructor

public class MpaService {
    private final MpaStorage mpaStorage;

    public Collection<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    public Mpa findById(Integer id) {
        return mpaStorage.findById(id);
    }
}
