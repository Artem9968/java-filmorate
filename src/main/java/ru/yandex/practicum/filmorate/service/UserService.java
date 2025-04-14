package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

    public User create(@Valid User user) {
        return userStorage.create(user);
    }


    public User update(@Valid User newUser) {
        return userStorage.update(newUser);
    }
    public User findById(Long id) throws NotFoundException, ValidationException {
        return userStorage.findById(id);
    }

    public void addFriend(Long userId, Long friendId) throws NotFoundException {
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) throws NotFoundException {
        userStorage.removeFriend(userId, friendId);
    }

    public Collection<User> getFriends(Long id) throws NotFoundException {
        return userStorage.getFriends(id);
    }

    public Collection<User> getCommonFriends(Long userId, Long otherUserId) throws NotFoundException {
        return userStorage.getCommonFriends(userId, otherUserId);
    }
}
