package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.model.UserMapper.dtoToUser;
import static ru.practicum.shareit.user.model.UserMapper.userToDto;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto getUserById(int userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        log.info("Запрошен пользователь {}", user);
        return userToDto(user);
    }

    @Override
    public List<UserDto> getUsers() {
        log.info("Запрошен список пользователей");
        return userStorage.getUsers().stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        String email = userDto.getEmail();
        if (userStorage.checkForAnEmail(email))
            throw new UserAlreadyExistsException("Пользователь с email '" + email + "' уже существует");
        User user = userStorage.addUser(
                dtoToUser(userDto)
        );
        log.info("Добавлен пользователь {}", user);
        return userToDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        int userId = userDto.getId();
        User oldUser = userStorage.getUserById(userId);
        if (oldUser == null) throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        User user = dtoToUser(userDto);
        user.setId(userId);
        String emailUpdate = user.getEmail();
        String nameUpdate = user.getName();

        String email = oldUser.getEmail();
        if (emailUpdate != null && !emailUpdate.isBlank()) {
            if (!emailUpdate.contentEquals(oldUser.getEmail()) && userStorage.checkForAnEmail(emailUpdate))
                throw new UserAlreadyExistsException("Пользователь с email '" + emailUpdate + "' уже существует");
            email = emailUpdate;
        }
        user.setEmail(email);

        String name = nameUpdate != null
                && !nameUpdate.isBlank()
                ? nameUpdate : oldUser.getName();
        user.setName(name);

        User updatedUser = userStorage.updateUser(user);
        log.info("Обновлен пользователь {}", updatedUser);
        return userToDto(updatedUser);
    }

    @Override
    public void deleteUser(int userId) {
        userStorage.deleteUser(userId);
        log.info("Удален пользователь с id={}", userId);
    }
}