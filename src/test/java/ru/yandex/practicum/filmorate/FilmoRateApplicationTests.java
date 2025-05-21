package ru.yandex.practicum.filmorate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
public class FilmoRateApplicationTests {

    @Autowired
    private UserDbStorage userDbStorage;

    @Test
    public void testFindUserById() throws NotFoundException {
        // Создаем пользователя через метод of
        User user = User.of(
                null, // ID будет сгенерирован базой данных
                "Test User",
                "test@example.com",
                "testLogin",
                LocalDate.of(1990, 1, 1),
                new HashSet<>(),
                new HashSet<>()
        );

        // Добавляем пользователя в базу данных
        User createdUser = userDbStorage.create(user);

        // Проверяем, что пользователь с ID = createdUser.getId() существует в базе данных
        User foundUser = userDbStorage.findById(createdUser.getId());

        // Проверяем, что найденный пользователь соответствует ожидаемым значениям
        assertThat(foundUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", createdUser.getId())
                .hasFieldOrPropertyWithValue("email", "test@example.com")
                .hasFieldOrPropertyWithValue("login", "testLogin")
                .hasFieldOrPropertyWithValue("name", "Test User")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 1, 1));
    }
}
