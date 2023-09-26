package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.model.UserMapper.dtoToUser;
import static ru.practicum.shareit.user.model.UserMapper.userToDto;

@Repository
@RequiredArgsConstructor
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private int idCounter = 1;
    private final Map<Integer, User> users;

    @Override
    public UserDto getUserById(int userId) {
        User user = users.get(userId);
        if (user == null) throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        log.info("Запрошен пользователь {}", user);
        return userToDto(user);
    }

    @Override
    public List<UserDto> getUsers() {
        log.info("Запрошен список пользователей");
        return users.values().stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        checkForAnEmail(userDto.getEmail());
        User user = dtoToUser(userDto);
        user.setId(idCounter);
        idCounter++;
        log.info("Добавлен пользователь {}", user);
        users.put(user.getId(), user);
        return userToDto(user);
    }

    @Override
    public UserDto updateUser(int userId, UserDto userDto) {
        User user = users.get(userId);
        if (user == null) throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        String emailUpdate = userDto.getEmail();
        String nameUpdate = userDto.getName();
        if (emailUpdate != null && !emailUpdate.isBlank() && !emailUpdate.contentEquals(user.getEmail())) {
            checkForAnEmail(emailUpdate);
            user.setEmail(emailUpdate);
        }
        String name = nameUpdate != null
                && !nameUpdate.isBlank()
                ? nameUpdate : user.getName();
        user.setName(name);
        log.info("Обновлен пользователь {}", user);
        return userToDto(user);
    }

    @Override
    public void deleteUser(int userId) {
        log.info("Удален пользователь {}", users.remove(userId));
    }

    private void checkForAnEmail(String email) {
        if (users.values().stream()
                .anyMatch(
                        user -> user.getEmail().equals(email)
                )
        ) throw new UserAlreadyExistsException("Пользователь с email '" + email + "' уже существует");
    }
}