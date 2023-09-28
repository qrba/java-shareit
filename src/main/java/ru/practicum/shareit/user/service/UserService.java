package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUserById(int userId);

    List<UserDto> getUsers();

    UserDto addUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    void deleteUser(int userId);
}