package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j(topic = "TRACE")
public class UserDbStorage implements UserStorage {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final JdbcTemplate jdbcTemplate;

    // Переименованные запросы в camelCase
    private final String selectAllUsers = "SELECT id, name, email, login, birthday FROM users";
    private final String selectAllFriendships = "SELECT userId, friendId FROM friends";
    private final String selectUserById = "SELECT id, name, email, login, birthday FROM users WHERE id = ?";
    private final String selectAllEmails = "SELECT email FROM users";
    private final String updateUser = "UPDATE users SET name = ?, email = ?, login = ?, birthday = ? WHERE id = ?";

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friends(new HashSet<>())
                .friendRequests(new HashSet<>())
                .build();
    }

    @Override
    public Collection<User> findAll() {
        log.info("Обработка Get-запроса...");
        Collection<User> users = jdbcTemplate.query(selectAllUsers, this::mapRowToUser);
        Map<Long, Set<Long>> friends = jdbcTemplate.query(selectAllFriendships, new FriendsExtractor());
        for (User user : users) {
            user.setFriends(friends.get(user.getId()));
        }
        return users;
    }

    @Override
    public User findById(Long id) {
        log.info("Обработка Get-запроса...");
        if (id != 0 && id != null) {
            try {
                jdbcTemplate.queryForObject(selectUserById, this::mapRowToUser, id);
            } catch (DataAccessException e) {
                log.error("Exception", new NotFoundException("Пользователь с данным идентификатором отсутствует в базе"));
                throw new NotFoundException("Пользователь с данным идентификатором отсутствует в базе");
            }
            User user = jdbcTemplate.queryForObject(selectUserById, this::mapRowToUser, id);
            Map<Long, Set<Long>> friends = jdbcTemplate.query(selectAllFriendships, new FriendsExtractor());
            user.setFriends(friends.get(id));
            return user;
        } else {
            log.error("Exception", new ConditionsNotMetException("Идентификатор пользователя не может быть нулевой"));
            throw new ConditionsNotMetException("Идентификатор пользователя не может быть нулевой");
        }
    }

    @Override
    public User create(@Valid User user) {
        log.info("Обработка Create-запроса...");
        validateUser(user);
        duplicateCheck(user);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> values = new HashMap<>();
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("birthday", user.getBirthday());

        Long id = simpleJdbcInsert.executeAndReturnKey(values).longValue();
        user.setId(id);
        return user;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null) {
            throw new ValidationException("Дата рождения не может быть нулевой");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private void duplicateCheck(User user) {
        Set<String> emails = jdbcTemplate.query(selectAllEmails, new EmailExtractor());
        if (emails.contains(user.getEmail())) {
            log.error("Exception", new DuplicatedDataException("Этот имейл уже используется"));
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
    }

    @Override
    public User update(@Valid User newUser) {
        log.info("Обработка Update-запроса...");

        if (newUser.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        User oldUser = findById(newUser.getId());
        if (oldUser == null) {
            throw new NotFoundException("Пользователь с указанным id не найден");
        }

        if (newUser.getEmail() == null || newUser.getEmail().isBlank() || !newUser.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (!newUser.getEmail().equals(oldUser.getEmail())) {
            duplicateCheck(newUser);
        }

        if (newUser.getLogin() == null || newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }

        if (newUser.getBirthday() == null) {
            throw new ValidationException("Дата рождения не может быть нулевой");
        }
        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        jdbcTemplate.update(updateUser,
                newUser.getName(),
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getBirthday(),
                newUser.getId());

        return newUser;
    }
}
