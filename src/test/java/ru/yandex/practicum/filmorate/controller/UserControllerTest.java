package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserControllerTest {

    public static UserController userController;
    static String name = "name1";
    static String email = "name112@mail.ru";
    static LocalDate data = LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    User user = User.of(Long.parseLong("0"), "name111", "name1@mail.ru", "name111@mail.ru", data, new HashSet<>());
    static User user10 = User.of(Long.parseLong("0"), "name111", "name1113@mail.ru", "name111@mail.ru", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")), new HashSet<>());
    User user1 = User.of(Long.parseLong("0"), name, "name1141@mail.ru", "name111@mail.ru", data, new HashSet<>());
    User user2 = User.of(Long.parseLong("0"), name, "name1311@mail.ru", "name111@mail.ru", data, new HashSet<>());
    User user3 = User.of(Long.parseLong("0"), name, email, " ", data, new HashSet<>());
    User user4 = User.of(Long.parseLong("0"), name, "name1851@mail.ru", "name111@mail.ru", data, new HashSet<>());
    User user5 = User.of(Long.parseLong("-1"), name, email, "name119@mail.ru", data, new HashSet<>());
    User user6 = User.of(Long.parseLong("-1"), name, email, "name111@mail.ruv", data, new HashSet<>());

    @BeforeAll
    public static void start() throws ValidationException, DuplicatedDataException {
        UserStorage userStorage = new InMemoryUserStorage();
        userController = new UserController(new UserService(userStorage));
        userController.create(user10);
    }

    @Test
    public void testValidate() throws ValidationException, DuplicatedDataException {
        assertEquals(userController.create(user), user);
    }

    @Test
    public void testMail() throws ValidationException, DuplicatedDataException {
        userController.create(user1);
    }

    @Test
    public void testDuplicate() throws ValidationException, DuplicatedDataException {
        userController.create(user2);
    }

    @Test
    public void testLogin() {
        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            User createdUser = userController.create(user3);
        }, "You can't created User without login");
    }

    @Test
    public void testBirthday() throws ValidationException, DuplicatedDataException {
        userController.create(user4);
    }

    @Test
    public void testNoId() throws ValidationException, NotFoundException, DuplicatedDataException {
        userController.create(user6);
    }

    @Test
    public void testWrongId() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            User createdUser = userController.update(user5);
        }, "You can't created User without login");
    }

    @Test
    public void testGet() {
        assertNotNull(userController.findAll());
    }
}