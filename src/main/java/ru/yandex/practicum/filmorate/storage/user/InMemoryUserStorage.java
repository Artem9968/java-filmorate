package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    public User create(User user) {
        log.info("Обработка Create-запроса...");
        duplicateCheck(user);
        user.setId(getNextId());
        user.setFriends(new HashSet<>());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }
        User oldUser = users.get(newUser.getId());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(newUser.getName() != null ? newUser.getName() : newUser.getLogin());
        oldUser.setBirthday(newUser.getBirthday());

        if (newUser.getFriends() == null) {
            newUser.setFriends(new HashSet<>());
        }
        oldUser.setFriends(new HashSet<>(newUser.getFriends()));

        users.put(oldUser.getId(), oldUser);
        return oldUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }

    private void duplicateCheck(User user) {
        for (User u : users.values()) {
            if (u.getEmail().equals(user.getEmail())) {
                throw new DuplicatedDataException("Эта почта уже используется");
            }
        }
    }

    @Override
    public User findById(Long id) throws NotFoundException {
        if (id == null) {
            throw new ValidationException("Id должен присутствовать");
        }
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
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

        log.info("Пользователь с ID = {} добавил в друзья пользователя с ID = {}", friendId, userId);
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

        log.info("Пользователь с ID = {} удалил из друзей пользователя с ID = {}", friendId, userId);
        return user;
    }

    @Override
    public List<User> getFriends(Long id) throws NotFoundException {
        User user = findById(id);

        if (user.getFriends() == null || user.getFriends().isEmpty()) {
            return Collections.emptyList();
        }

        return user.getFriends().stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) throws NotFoundException {
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
