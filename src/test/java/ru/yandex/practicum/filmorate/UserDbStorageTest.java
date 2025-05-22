package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Sql(scripts = {"/schema.sql", "/data.sql"}) // Загружаем схему и тестовые данные
class UserDbStorageTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UserDbStorage userDbStorage;

    @BeforeEach
    void setUp() {
        userDbStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    void testCreateUser() throws ValidationException, DuplicatedDataException {
        // Создаем нового пользователя с использованием метода User.of(...)
        User user = User.of(
                null,
                "Test User",
                "test@example.com",
                "testLogin",
                LocalDate.of(1990, 1, 1),
                new HashSet<>(), // Инициализируем пустое множество friends
                new HashSet<>()  // Инициализируем пустое множество friendRequests
        );

        User createdUser = userDbStorage.create(user);

        assertNotNull(createdUser.getId());
        assertEquals("test@example.com", createdUser.getEmail());
        assertEquals("testLogin", createdUser.getLogin());
        assertEquals("Test User", createdUser.getName());
        assertEquals(LocalDate.of(1990, 1, 1), createdUser.getBirthday());
    }

    @Test
    void testUpdateUser() throws ValidationException, DuplicatedDataException, NotFoundException {
        // Создаем нового пользователя с использованием метода User.of(...)
        User user = User.of(
                null,
                "Test User",
                "test@example.com",
                "testLogin",
                LocalDate.of(1990, 1, 1),
                new HashSet<>(),
                new HashSet<>()
        );
        User createdUser = userDbStorage.create(user);

        // Обновляем пользователя с использованием метода User.of(...)
        User updatedUser = User.of(
                createdUser.getId(),
                "Updated User",
                "test@example.com",
                "testLogin",
                LocalDate.of(1990, 1, 1),
                new HashSet<>(),
                new HashSet<>()
        );
        User result = userDbStorage.update(updatedUser);

        assertEquals("Updated User", result.getName());
    }

    @Test
    void testFindById() throws ValidationException, DuplicatedDataException, NotFoundException {
        // Создаем нового пользователя с использованием метода User.of(...)
        User user = User.of(
                null,
                "Test User",
                "test@example.com",
                "testLogin",
                LocalDate.of(1990, 1, 1),
                new HashSet<>(),
                new HashSet<>()
        );
        User createdUser = userDbStorage.create(user);

        User foundUser = userDbStorage.findById(createdUser.getId());

        assertEquals(createdUser.getId(), foundUser.getId());
        assertEquals("test@example.com", foundUser.getEmail());
        assertEquals("testLogin", foundUser.getLogin());
        assertEquals("Test User", foundUser.getName());
        assertEquals(LocalDate.of(1990, 1, 1), foundUser.getBirthday());
    }

    @Test
    void testFindByIdNotFound() {
        assertThrows(NotFoundException.class, () -> userDbStorage.findById(999L));
    }
}
