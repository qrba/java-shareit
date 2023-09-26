package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

public interface UserStorage {
    UserDto getUserById(int userId);

    List<UserDto> getUsers();

    UserDto addUser(UserDto userDto);

    UserDto updateUser(int userId, UserDto userDto);

    void deleteUser(int userId);
}