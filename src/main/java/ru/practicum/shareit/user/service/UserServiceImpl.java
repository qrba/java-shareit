package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto getUserById(int userId) {
        return userStorage.getUserById(userId);
    }

    @Override
    public List<UserDto> getUsers() {
        return userStorage.getUsers();
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        return userStorage.addUser(userDto);
    }

    @Override
    public UserDto updateUser(int userId, UserDto userDto) {
        return userStorage.updateUser(userId, userDto);
    }

    @Override
    public void deleteUser(int userId) {
        userStorage.deleteUser(userId);
    }
}