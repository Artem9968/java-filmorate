package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) throws ValidationException, DuplicatedDataException {

        validateEmail(user.getEmail());
        validateLogin(user.getLogin());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        validateBirthday(user.getBirthday());
        return userStorage.create(user);
    }

    public User update(User newUser) throws NotFoundException, ValidationException {
        if (newUser.getId() == null) {
            throw new ValidationException("Id должен присутствовать");
        }

        return userStorage.update(newUser);
    }

    public User findById(Long id) throws NotFoundException, ValidationException {
        if (id == null) {
            throw new ValidationException("Id должен присутствовать");
        }
        return userStorage.findById(id);
    }

    public void addFriend(Long userId, Long friendId) throws NotFoundException {
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) throws NotFoundException {
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long id) throws NotFoundException {
        return userStorage.getFriends(id);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) throws NotFoundException {
        return userStorage.getCommonFriends(userId, otherUserId);
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@") || email.contains(" ") || email.length() == 1) {
            throw new ValidationException("Неверный формат электронной почты");
        }
    }

    private void validateLogin(String login) {
        if (login == null || login.contains(" ") || login.isBlank()) {
            throw new ValidationException("Логин не должен быть пустым или состоять из пробелов");
        }
    }

    private void validateBirthday(LocalDate birthday) {
        if (birthday == null) {
            throw new ValidationException("Дата рождения должна быть указана");
        }
        if (birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не должна быть из будущего");
        }
    }
}
