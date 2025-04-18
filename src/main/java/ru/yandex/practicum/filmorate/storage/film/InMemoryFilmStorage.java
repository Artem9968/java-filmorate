package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

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

    @Override
    public Collection<Film> getAllFilms() {
        log.info("Обработка Get-запроса...");
        return films.values();
    }

    @Override
    public Film createFilm(Film film) {
        log.info("Обработка Create-запроса...");
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        log.info("Обработка Put-запроса...");
        Film oldFilm = films.get(newFilm.getId());
        if (oldFilm == null) {
            throw new NotFoundException("Фильм не найден");
        }
        films.put(oldFilm.getId(), newFilm);
        return newFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Film findById(Long id) throws NotFoundException {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Такого фильма не существует");
        }
        return film;
    }

    @Override
    public void addLike(Long filmId, Long userId) throws NotFoundException {
        Film film = findById(filmId); // проверяем, существует ли фильм
        if (film == null) {
            throw new NotFoundException("Такого фильма не существует");
        }

        film.getLikedUsers().add(userId);
        log.info("Пользователь с ID = {} поставил лайк фильму с ID = {}", userId, filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) throws NotFoundException {
        Film film = findById(filmId);
        if (film == null) {
            throw new NotFoundException("Такого фильма не существует");
        }

        if (!film.getLikedUsers().contains(userId)) {
            throw new NotFoundException(String.format("Лайк отсутствует", userId, filmId));
        }

        film.getLikedUsers().remove(userId);
        log.info("Пользователь с ID = {} удалил лайк фильму с ID = {}", userId, filmId);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        log.info("Топ фильмов", count);
        return films.values().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikedUsers().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
