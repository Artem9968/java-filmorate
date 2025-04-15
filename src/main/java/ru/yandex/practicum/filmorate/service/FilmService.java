package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film create(Film film) throws ValidationException {
        return filmStorage.createFilm(film);
    }

    public Film update(Film newFilm) throws NotFoundException, ValidationException {
        return filmStorage.update(newFilm);
    }

    public Film getFilmById(Long id) throws NotFoundException {
        return filmStorage.findById(id);
    }

    public void addLike(Long filmId, Long userId) throws NotFoundException {
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) throws NotFoundException {
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getTopFilms(count);
    }
}