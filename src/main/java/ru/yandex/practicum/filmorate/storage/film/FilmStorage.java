package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Collection<Film> getAllFilms();

    Film findById(Long id) throws NotFoundException;

    Film createFilm(Film film);

    Film update(Film film) throws NotFoundException;

    //Film
    void addLike(Long filmId, Long userId) throws NotFoundException;

    void removeLike(Long filmId, Long userId) throws NotFoundException;

    List<Film> getTopFilms(int count);
}
