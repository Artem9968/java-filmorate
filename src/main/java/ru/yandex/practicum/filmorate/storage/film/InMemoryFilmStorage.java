package ru.yandex.practicum.filmorate.storage.film;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private static final Map<Long, Film> films = new HashMap<>();
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<Film> getAllFilms() {
        log.info("Обработка Get-запроса...");
        return films.values();
    }

    @Override
    public Film createFilm(Film film) {
        log.info("Обработка Create-запроса...");
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(@Valid Film newFilm) {
        log.info("Обработка Put-запроса...");
        validateFilmId(newFilm.getId());

        Film oldFilm = films.get(newFilm.getId());
        if (oldFilm == null) {
            throw new NotFoundException("Фильм не найден");
        }
        films.put(oldFilm.getId(), newFilm);
        return newFilm;
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

    private long getNextId() {
        long currentMaxId = films.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Film findById(Long id) {
        if (id == null) {
            throw new ValidationException("ID фильма не должно быть пустым");
        }
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Такого фильма не существует");
        }
        return film;
    }

    @Override
    public void addLike(Long filmId, Long userId) throws NotFoundException {
        Film film = findById(filmId); // Проверяем, существует ли фильм
        if (film == null) {
            throw new NotFoundException("Такого фильма не существует");
        }

        User user = userStorage.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("not found", userId));
        }

        film.getLikedUsers().add(userId);
        log.info("User with ID = {} liked the film with ID = {}", userId, filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) throws NotFoundException {
        Film film = findById(filmId);
        if (film == null) {
            throw new NotFoundException("Такого фильма не существует");
        }

        User user = userStorage.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("not found", userId));
        }

        if (!film.getLikedUsers().contains(userId)) {
            throw new NotFoundException(String.format("User with ID = %d did not like the film with ID = %d", userId, filmId));
        }

        film.getLikedUsers().remove(userId);
        log.info("User with ID = {} unliked the film with ID = {}", userId, filmId);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        log.info("Getting top-{} films by number of likes", count);
        return films.values().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikedUsers().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
