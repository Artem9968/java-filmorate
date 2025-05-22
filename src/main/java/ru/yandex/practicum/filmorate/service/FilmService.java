package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "TRACE")
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaService mpaService;
    private final GenreService genreService;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Long id) {
        return filmStorage.findById(id);
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        if (filmStorage.findById(film.getId()) != null) {
            validateFilm(film);
            return filmStorage.update(film);
        }
        throw new NotFoundException("Пост с id = " + film.getId() + " не найден");

    }

    public Film addLike(Integer idFilm, Long idUser) {
        User user = userStorage.findById(idUser);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + idUser + " не найден");
        }
        Film film;
        try {
            film = filmStorage.findById(Long.valueOf(idFilm));
            if (film == null) {
                throw new NotFoundException("Фильм с id = " + idFilm + " не найден");
            }
        } catch (Exception e) {
            throw new NotFoundException("Фильм не найден");
        }
        Set<Long> filmLikesId = film.getLikes();
        filmLikesId.add(idUser);
        film.setLikes(filmLikesId);
        return filmStorage.update(film);
    }

    public Film deleteById(Long id) {
        Film film = filmStorage.findById(id);
        filmStorage.deleteById(id);
        return film;
    }

    public Film deleteLike(Long idFilm, Long idUser) {
        User user = userStorage.findById(idUser);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + idUser + " не найден");
        }
        Film film = filmStorage.findById(idFilm);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + idFilm + " не найден");
        }
        return film;
    }

    public List<Film> getTop(Integer count) {
        return filmStorage
                .findAll()
                .stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film p0, Film p1) {
        return p1.getLikes().size() - p0.getLikes().size(); //прямой порядок сортировки
    }

    private void validateFilm(Film film) {
        if (film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Длина описания превышает 200 символов");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года;");
        }
        if (mpaService.findById(film.getMpa().getId()) == null) throw new NotFoundException("Mpa not found");
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (genreService.findById(genre.getId()) == null) {
                    throw new ValidationException("Genre not found");
                }
            }
        }
    }
}