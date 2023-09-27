package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User getUserById(int userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        log.info("Запрошен пользователь {}", user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        log.info("Запрошен список пользователей");
        return userStorage.getUsers();
    }

    @Override
    public User addUser(User user) {
        User addedUser = userStorage.addUser(user);
        log.info("Добавлен пользователь {}", addedUser);
        return addedUser;
    }

    @Override
    public User updateUser(User user) {
        getUserById(user.getId());
        User updatedUser = userStorage.updateUser(user);
        log.info("Обновлен пользователь {}", updatedUser);
        return updatedUser;
    }

    @Override
    public void deleteUser(int userId) {
        userStorage.deleteUser(userId);
        log.info("Удален пользователь с id={}", userId);
    }
}