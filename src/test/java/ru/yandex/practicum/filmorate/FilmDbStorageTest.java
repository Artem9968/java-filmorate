package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;

import java.util.LinkedHashSet;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmDbStorageTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private FilmDbStorage filmDbStorage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filmDbStorage = new FilmDbStorage(jdbcTemplate);
    }

    @Test
    void createFilmWithEmptyNameShouldThrowException() {
        Film film = new Film(
                1L,
                "New film",
                "New Description",
                LocalDate.of(3000, 8, 1),
                1,
                new LinkedHashSet<Genre>(),
                new Mpa(),
                Set.of(3L)
        );

        assertThrows(RuntimeException.class, () -> filmDbStorage.create(film));
    }

    @Test
    void createFilmWithLongDescriptionShouldThrowException() {
        Film film = new Film(
                1L,
                "New film",
                "New Description",
                LocalDate.of(3000, 8, 1),
                1,
                new LinkedHashSet<Genre>(),
                new Mpa(),
                Set.of(3L)
        );


        assertThrows(RuntimeException.class, () -> filmDbStorage.create(film));
    }

    @Test
    void createFilmWithInvalidReleaseDateShouldThrowException() {
        Film film = new Film(
                1L,
                "New film",
                "New Description",
                LocalDate.of(3000, 8, 1),
                1,
                new LinkedHashSet<Genre>(),
                new Mpa(),
                Set.of(3L)
        );
        assertThrows(RuntimeException.class, () -> filmDbStorage.create(film));
    }

    @Test
    void createFilmWithNegativeDurationShouldThrowException() {
        Film film = new Film(
                1L,
                "New film",
                "New Description",
                LocalDate.of(3000, 8, 1),
                1,
                new LinkedHashSet<Genre>(),
                new Mpa(),
                Set.of(3L)
        );

        assertThrows(RuntimeException.class, () -> filmDbStorage.create(film));
    }

    @Test
    void createFilmWithInvalidMpaShouldThrowException() {
        Film film = new Film(
                1L,
                "New film",
                "New Description",
                LocalDate.of(3000, 8, 1),
                1,
                new LinkedHashSet<Genre>(),
                new Mpa(),
                Set.of(3L)
        );

        assertThrows(RuntimeException.class, () -> filmDbStorage.create(film));
    }
}
