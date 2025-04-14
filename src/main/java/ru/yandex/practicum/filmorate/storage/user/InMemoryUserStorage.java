package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        log.info("Обработка Get-запроса...");
        return users.values();
    }

    @Override
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

    @Override
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

    @Override
    public User findById(Long id) throws NotFoundException {
        if (id == null) {
            throw new ValidationException("ID cannot be null");
        }
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("User with ID = " + id + " not found");
        }
        log.info("User found: {}", user);
        return user;
    }

    @Override
    public void addFriend(Long userId, Long friendId) throws NotFoundException {
        User user = findById(userId);
        User friend = findById(friendId);

        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if (friend.getFriends() == null) {
            friend.setFriends(new HashSet<>());
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        update(user);
        update(friend);

        log.info("User with ID = {} added as a friend to user with ID = {}", friendId, userId);
    }

    @Override
    public User removeFriend(Long userId, Long friendId) throws NotFoundException {
        User user = findById(userId);
        User friend = findById(friendId);

        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if (friend.getFriends() == null) {
            friend.setFriends(new HashSet<>());
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        log.info("User with ID = {} has been removed from friends of user with ID = {}", friendId, userId);
        return user;
    }

    @Override
    public Collection<User> getFriends(Long id) throws NotFoundException {
        User user = findById(id);

        if (user.getFriends() == null || user.getFriends().isEmpty()) {
            return Collections.emptyList();
        }

        return user.getFriends().stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherUserId) throws NotFoundException {
        User user = findById(userId);
        User otherUser = findById(otherUserId);

        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if (otherUser.getFriends() == null) {
            otherUser.setFriends(new HashSet<>());
        }

        Set<Long> commonFriendIds = new HashSet<>(user.getFriends());
        commonFriendIds.retainAll(otherUser.getFriends());

        return commonFriendIds.stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }
}
