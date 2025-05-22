package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendsExtractor;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j(topic = "TRACE")
@RequiredArgsConstructor
public class UserService {

    //sQL-запросы
    private static final String SQL_SELECT_FRIENDS = "select userId, friendId from friends";
    private static final String SQL_INSERT_FRIEND = "insert into friends(userId, friendId) values (?, ?)";
    private static final String SQL_DELETE_FRIEND = "delete from friends where userId = ? and friendId = ?";

    //сообщения для логирования и исключений
    private static final String LOG_POST_REQUEST = "Обработка Post-запроса...";
    private static final String LOG_DELETE_REQUEST = "Обработка Del-запроса...";
    private static final String LOG_GET_REQUEST = "Обработка Get-запроса...";
    private static final String ERROR_FRIEND_ALREADY_ADDED = "Пользователь с id %d уже добавлен в друзья";
    private static final String ERROR_USER_NOT_FOUND = "Пользователь с данным идентификатором отсутствует в базе";

    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    public Collection<User> findAllUsers() {
        return userStorage.findAll();
    }

    public User findUserById(Long id) {
        User user = userStorage.findById(id);
        if (user == null) {
            throw new NotFoundException("User not found with id: " + id);
        }
        return user;
    }

    public User createUser(User user) {
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        validateUserExists(user.getId()); // Добавляем проверку
        return userStorage.update(user);
    }

    public User addFriend(Long idUser, Long idFriend) {
        log.info(LOG_POST_REQUEST);

        validateUserExists(idUser);
        validateUserExists(idFriend);

        if (isFriend(idUser, idFriend)) {
            logAndThrowConditionsNotMetException(String.format(ERROR_FRIEND_ALREADY_ADDED, idFriend));
        }

        jdbcTemplate.update(SQL_INSERT_FRIEND, idUser, idFriend);
        return userStorage.findById(idUser);
    }

    public User delFriend(Long idUser, Long idFriend) {
        log.info(LOG_DELETE_REQUEST);

        validateUserExists(idUser);
        validateUserExists(idFriend);

        jdbcTemplate.update(SQL_DELETE_FRIEND, idUser, idFriend);
        return userStorage.findById(idUser);
    }

    public Set<User> findJointFriends(Long idUser, Long idFriend) {
        log.info(LOG_GET_REQUEST);

        validateUserExists(idUser);
        validateUserExists(idFriend);

        String sqlSelectJointFriends = "SELECT f1.friendId AS jointFriendId " +
                "FROM friends f1 " +
                "JOIN friends f2 ON f1.friendId = f2.friendId " +
                "WHERE f1.userId = ? AND f2.userId = ?";

        Set<Long> jointFriendIds = new HashSet<>(jdbcTemplate.queryForList(
                sqlSelectJointFriends, Long.class, idUser, idFriend));

        Set<User> result = new HashSet<>();
        for (Long friendId : jointFriendIds) {
            result.add(userStorage.findById(friendId));
        }
        return result;
    }

    public Set<User> findAllFriends(Long idUser) {
        log.info(LOG_GET_REQUEST);

        validateUserExists(idUser);

        Map<Long, Set<Long>> friends = jdbcTemplate.query(SQL_SELECT_FRIENDS, new FriendsExtractor());
        assert friends != null;
        Set<Long> userFriends = friends.getOrDefault(idUser, new HashSet<>());

        Set<User> result = new HashSet<>();
        for (Long friendId : userFriends) {
            result.add(userStorage.findById(friendId));
        }
        return result;
    }

    private void validateUserExists(Long userId) {
        if (userStorage.findById(userId) == null) {
            logAndThrowNotFoundException(userId.toString());
        }
    }

    private boolean isFriend(Long idUser, Long idFriend) {
        Map<Long, Set<Long>> friends = jdbcTemplate.query(SQL_SELECT_FRIENDS, new FriendsExtractor());
        assert friends != null;
        return friends.getOrDefault(idUser, new HashSet<>()).contains(idFriend);
    }

    private void logAndThrowConditionsNotMetException(String message) {
        log.error("Exception", new ConditionsNotMetException(message));
        throw new ConditionsNotMetException(message);
    }

    private void logAndThrowNotFoundException(String value) {
        log.error("Exception", new NotFoundException(UserService.ERROR_USER_NOT_FOUND));
        throw new NotFoundException(UserService.ERROR_USER_NOT_FOUND);
    }
}
