package ru.practicum.shareit.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageTest {
    private final UserStorage userStorage;

    @Test
    void shouldAddUserAndGetUserById() {
        UserDto userDto = new UserDto(1, "Test username", "test@email.com");
        userStorage.addUser(userDto);
        UserDto userDtoById = userStorage.getUserById(1);

        assertEquals(userDto, userDtoById);
    }

    @Test
    void shouldNotGetUserByIdWhenIncorrectId() {
        UserNotFoundException e = assertThrows(
                UserNotFoundException.class,
                () -> userStorage.getUserById(10)
        );

        assertEquals("Пользователь с id=10 не найден", e.getMessage());
    }

    @Test
    void shouldGetUsers() {
        UserDto userDto = new UserDto(1, "Test username", "test@email.com");
        userStorage.addUser(userDto);
        List<UserDto> users = userStorage.getUsers();

        assertEquals(1, users.size());
        assertEquals(userDto, users.get(0));
    }

    @Test
    void shouldNotAddUserWhenEmailExists() {
         UserAlreadyExistsException e = assertThrows(
                UserAlreadyExistsException.class,
                () -> {
                    UserDto userDto = new UserDto(1, "Test username", "test@email.com");
                    userStorage.addUser(userDto);
                    UserDto newUserDto = new UserDto(1, "New test username", "test@email.com");
                    userStorage.addUser(newUserDto);
                }
        );

        assertEquals("Пользователь с email 'test@email.com' уже существует", e.getMessage());
    }

    @Test
    void shouldUpdateUser() {
        UserDto userDto = new UserDto(1, "Test username", "test@email.com");
        userStorage.addUser(userDto);
        UserDto userDtoToUpdate = new UserDto(1, "Updated test username", "test@email.com");
        userStorage.updateUser(1, userDtoToUpdate);
        UserDto updatedUserDto = userStorage.getUserById(1);

        assertEquals(userDtoToUpdate, updatedUserDto);
    }

    @Test
    void shouldNotUpdateUserWhenIncorrectId() {
        UserNotFoundException e = assertThrows(
                UserNotFoundException.class,
                () -> {
                    UserDto userDtoToUpdate = new UserDto(1, "Updated test username", "test@email.com");
                    userStorage.updateUser(1, userDtoToUpdate);
                }
        );

        assertEquals("Пользователь с id=1 не найден", e.getMessage());
    }

    @Test
    void shouldDeleteUser() {
        UserNotFoundException e = assertThrows(
                UserNotFoundException.class,
                () -> {
                    UserDto userDto = new UserDto(1, "Test username", "test@email.com");
                    userStorage.addUser(userDto);
                    userStorage.deleteUser(1);
                    userStorage.getUserById(1);
                }
        );

        assertEquals("Пользователь с id=1 не найден", e.getMessage());
    }
}
