package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTest {

    private static FilmController filmController;
    private static Film validFilm;

    @BeforeAll
    public static void start() throws ValidationException {
        // Создаем мок для UserStorage
        UserStorage userStorage = Mockito.mock(UserStorage.class);

        // Создаем экземпляр хранилища фильмов, передавая мок UserStorage
        FilmStorage filmStorage = new InMemoryFilmStorage(userStorage);

        // Создаем экземпляр сервиса, передавая ему хранилище
        FilmService filmService = new FilmService(filmStorage);

        // Создаем контроллер, передавая ему сервис
        filmController = new FilmController(filmService);

        // Инициализация тестовых данных
        validFilm = new Film();
        validFilm.setId(0L);
        validFilm.setName("Фильм");
        validFilm.setDescription("Описание");
        validFilm.setReleaseDate(LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        validFilm.setDuration(100);
        filmController.createFilm(validFilm);

    }

        @Test
        public void shouldCreateValidFilm () throws ValidationException {
            assertEquals(filmController.createFilm(validFilm), validFilm);
        }
    }