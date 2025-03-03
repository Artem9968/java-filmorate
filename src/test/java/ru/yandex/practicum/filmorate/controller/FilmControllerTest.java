package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class FilmControllerTest {

    public static FilmController filmController = new FilmController(new FilmService());
    Film film = Film.of(Long.parseLong("0"), "name", "description ", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")), 100);
    Film film1 = Film.of(Long.parseLong("0"), "ко65", "description ", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")), 100);
    Film film2 = Film.of(Long.parseLong("0"), "name", "descripti", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")), 100);
    Film film3 = Film.of(Long.parseLong("0"), "name", "description ", LocalDate.parse("1990-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")), 100);
    Film film4 = Film.of(Long.parseLong("0"), "name", "description ", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")), 1);
    static Film film5 = Film.of(Long.parseLong("0"), "name111", "description ", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")), 100);
    Film film6 = Film.of(Long.parseLong("1"), " ", "description ", LocalDate.parse("2029-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")), 100);
    Film film7 = Film.of(Long.parseLong("1"), "name", "description ", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")), 100);

    @BeforeAll
    public static void start() throws ValidationException, NotFoundException {
        filmController.createFilm(film5);
    }

    @Test
    public void testValidate() throws ValidationException, NotFoundException {
        assertEquals(filmController.createFilm(film), film);
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
        assertEquals(filmController.update(film6), film6);
    }

    @Test
    public void testWrongId() throws ValidationException, NotFoundException {
        filmController.update(film7);
    }

    @Test
    public void testGet() throws ValidationException, NotFoundException {
        assertNotNull(filmController.getFilms());
    }
}