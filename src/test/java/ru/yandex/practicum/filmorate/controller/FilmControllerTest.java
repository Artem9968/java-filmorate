package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FilmControllerTest {

    private static FilmController filmController;
    private static Film film1;
    private static Film film2;
    private static Film film3;
    private static Film film4;

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

        film1 = new Film();
        film2 = new Film();
        film3 = new Film();
        film4 = new Film();

        film1.setId(0L);
        film1.setName("Фильм");
        film1.setDescription("Описание");
        film1.setReleaseDate(LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        film1.setDuration(100);

        film2.setId(Long.parseLong("0"));
        film2.setName("Фильм");
        film2.setDescription("description ");
        film2.setReleaseDate(LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        film2.setDuration(100);

        film3.setId(Long.parseLong("1"));
        film3.setName("Фильм");
        film3.setDescription("Описание");
        film3.setReleaseDate(LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        film3.setDuration(100);
        filmController.createFilm(film3);

        film4.setId(0L);
        film4.setName("name");
        film4.setDescription("description ");
        film4.setReleaseDate(LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        film4.setDuration(100);
    }

    @Test
    public void testValidate() throws ValidationException, NotFoundException {
        assertEquals(filmController.createFilm(film1), film1);
    }

    @Test
    public void testName() throws ValidationException, NotFoundException {
        filmController.createFilm(film1);
    }

    @Test
    public void testDescription() throws ValidationException, NotFoundException {
        assertNotNull(filmController.createFilm(film2));
    }

    @Test
    public void testReleaseDate() throws ValidationException, NotFoundException {
        filmController.createFilm(film3);
    }

    @Test
    public void testDuration() throws ValidationException, NotFoundException {
        filmController.createFilm(film4);
    }

    @Test
    public void testNoId() {
        assertEquals(filmController.update(film1), film1);
    }

    @Test
    public void testWrongId() throws ValidationException, NotFoundException {
        filmController.update(film3);
    }

    @Test
    public void testGet() throws ValidationException, NotFoundException {
        assertNotNull(filmController.getFilms());
    }
}