package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.model.UserMapper.userFromDto;
import static ru.practicum.shareit.user.model.UserMapper.userToDto;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(int userId) {
        Optional<User> userOptional = userStorage.findById(userId);
        if (userOptional.isEmpty()) throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        User user = userOptional.get();
        log.info("Запрошен пользователь {}", user);
        return userToDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        log.info("Запрошен список пользователей");
        return userStorage.findAll().stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        try {
            User user = userStorage.save(
                    userFromDto(userDto)
            );
            log.info("Добавлен пользователь {}", user);
            return userToDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("Пользователь с email '" + userDto.getEmail() + "' уже существует");
        }
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        try {
            int userId = userDto.getId();
            Optional<User> userOptional = userStorage.findById(userId);
            if (userOptional.isEmpty()) throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
            User oldUser = userOptional.get();
            User updateUser = userFromDto(userDto);

            String email = updateUser.getEmail();
            String name = updateUser.getName();
            if (email == null || email.isBlank()) updateUser.setEmail(oldUser.getEmail());
            if (name == null || name.isBlank()) updateUser.setName(oldUser.getName());

            User user = userStorage.save(updateUser);
            log.info("Обновлен пользователь {}", user);
            return userToDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("Пользователь с email '" + userDto.getEmail() + "' уже существует");
        }
    }

    @Override
    public void deleteUser(int userId) {
        userStorage.deleteById(userId);
        log.info("Удален пользователь с id={}", userId);
    }
}