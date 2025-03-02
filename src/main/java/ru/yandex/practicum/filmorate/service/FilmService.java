package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class FilmService {

    private  final Map<Long, Film> films = new HashMap<>();

    public Collection<Film> getAllFilms() {
        log.info("Обработка Get-запроса...");
        return films.values();
    }

    public Film create(@Valid Film film) {
        log.info("Обработка Create-запроса...");
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    public Film update(@Valid Film newFilm) {
        log.info("Обработка Put-запроса...");
        validateFilmId(newFilm.getId());

        Film oldFilm = films.get(newFilm.getId());
        if (oldFilm == null) {
            throw new NotFoundException("Фильм не найден");
        }

        updateFilmDetails(oldFilm, newFilm);
        return oldFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }

    private void validateFilmId(Long id) {
        if (id == null) {
            logAndThrow("Идентификатор фильма не может быть null");
        }
    }

    private void validateFilm(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            logAndThrow("Название фильма не должно быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            logAndThrow("Описание фильма не должно превышать 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            logAndThrow("Дата релиза фильма не должна быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            logAndThrow("Продолжительность фильма может быть только положительным числом");
        }
    }

    private void logAndThrow(String message) {
        log.error(message);
        throw new ValidationException(message);
    }

    private void updateFilmDetails(Film oldFilm, Film newFilm) {
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
    }
}
