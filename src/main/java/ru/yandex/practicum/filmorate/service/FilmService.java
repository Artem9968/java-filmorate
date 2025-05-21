package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
     private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage,UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film create(Film film) throws ValidationException {
        validateFilm(film);
        return filmStorage.createFilm(film);
    }

    public Film update(Film newFilm) throws NotFoundException, ValidationException {
        validateFilmId(newFilm.getId());
        return filmStorage.update(newFilm);
    }

    public Film getFilmById(Long id) throws NotFoundException {
        if (id == null) {
            throw new ValidationException("ID фильма не должно быть пустым");
        }
        return filmStorage.findById(id);
    }

    public void addLike(Long filmId, Long userId) throws NotFoundException {
        User user = userStorage.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("не найден", userId));
        }
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) throws NotFoundException {
        User user = userStorage.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователя нет", userId));
        }
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getTopFilms(count);
    }

    private void validateFilmId(Long id) {
        if (id == null) {
            throw new ValidationException("Идентификатор фильма не может быть null");
        }
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не должно быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не должно превышать 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза фильма не должна быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма может быть только положительным числом");
        }
    }
}