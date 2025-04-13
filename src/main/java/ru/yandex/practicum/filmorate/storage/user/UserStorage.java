package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> findAll();

    User create(User user);

    User update(User user) throws NotFoundException;

    void addFriend(Long userId, Long friendId) throws NotFoundException;

    User removeFriend(Long userId, Long friendId) throws NotFoundException;

    Collection<User> getCommonFriends(Long userId, Long otherUserId) throws NotFoundException;

    User findById(Long id) throws NotFoundException;

    Collection<User> getFriends(Long id) throws NotFoundException;
}
