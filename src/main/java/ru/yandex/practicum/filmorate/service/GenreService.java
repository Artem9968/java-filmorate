package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;

import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;


@Service
@RequiredArgsConstructor
@Slf4j
public class GenreService {
    private final GenreStorage genreStorage;

    public Collection<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Genre findById(Integer id) {
        return genreStorage.findById(id);
    }

}
