package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserService {

    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        log.info("Обработка Get-запроса...");
        return users.values();
    }

    public User create(@Valid User user) {
        log.info("Обработка Create-запроса...");
        duplicateCheck(user);

        validateEmail(user.getEmail());
        validateLogin(user.getLogin());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        validateBirthday(user.getBirthday());

        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }

    private void duplicateCheck(User user) {
        for (User u : users.values()) {
            if (u.getEmail().equals(user.getEmail())) {
                logAndThrow(new DuplicatedDataException("Эта почта уже используется"));
            }
        }
    }

    public User update(@Valid User newUser) {
        if (newUser.getId() == null) {
            logAndThrow(new ValidationException("Id должен присутствовать"));
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName() != null ? newUser.getName() : newUser.getLogin());
            oldUser.setBirthday(newUser.getBirthday());
            return oldUser;
        } else {
            logAndThrow(new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден"));
        }
        return null;
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@") || email.contains(" ") || email.length() == 1) {
            logAndThrow(new ValidationException("Неверный формат электронной почты"));
        }
    }

    private void validateLogin(String login) {
        if (login == null || login.contains(" ") || login.isBlank()) {
            logAndThrow(new ValidationException("Логин не должен быть пустым или состоять из пробелов"));
        }
    }

    private void validateBirthday(LocalDate birthday) {
        if (birthday != null) {
            if (birthday.isAfter(LocalDate.now())) {
                logAndThrow(new ValidationException("Дата рождения не должна быть из будущего"));
            }
        } else {
            logAndThrow(new ValidationException("Дата рождения должна быть указана"));
        }
    }

    private void logAndThrow(RuntimeException exception) {
        log.error(exception.getMessage());
        throw exception;
    }
}
