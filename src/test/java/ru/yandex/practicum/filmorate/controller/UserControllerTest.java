package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserControllerTest {

    public static UserController userController = new UserController(new UserService());
    User user = User.of(Long.parseLong("0"), "name111", "name1111@mail.ru", "name111@mail.ru", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    static User user10 = User.of(Long.parseLong("0"), "name111", "name1113@mail.ru", "name111@mail.ru", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    User user1 = User.of(Long.parseLong("0"), "name1", "name1211@mail.ru", "name111@mail.ru", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    User user2 = User.of(Long.parseLong("0"), "name1", "name1311@mail.ru", "name111@mail.ru", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    User user3 = User.of(Long.parseLong("0"), "name1", "name1641@mail.ru", " ", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    User user4 = User.of(Long.parseLong("0"), "name1", "name1851@mail.ru", "name111@mail.ru", LocalDate.parse("2021-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    User user5 = User.of(Long.parseLong("-1"), "name1", "name1611@mail.ru", "name119@mail.ru", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    User user6 = User.of(Long.parseLong("-1"), "name1", "name9191@mail.ru", "name111@mail.ruv", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

    @BeforeAll
    public static void start() throws ValidationException, DuplicatedDataException {
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

    @AfterAll
    public static void testGet() {
        assertNotNull(userController.findAll());
    }
}