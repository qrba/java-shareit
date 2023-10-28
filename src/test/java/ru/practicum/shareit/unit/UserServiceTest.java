package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static ru.practicum.shareit.user.model.UserMapper.userToDto;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserStorage userStorage;
    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User(1, "user1", "user1@email.com");
    }

    @Test
    public void shouldGetUserById() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.of(user));

        UserDto userDtoOutgoing = userService.getUserById(1);

        assertThat(user.getId(), equalTo(userDtoOutgoing.getId()));
        assertThat(user.getName(), equalTo(userDtoOutgoing.getName()));
        assertThat(user.getEmail(), equalTo(userDtoOutgoing.getEmail()));
    }

    @Test
    public void shouldNotGetUserByIdWhenUserNotFound() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.empty());

        UserNotFoundException e = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(1)
        );

        assertEquals("Пользователь с id=1 не найден", e.getMessage());
    }

    @Test
    public void shouldGetUsers() {
        Mockito
                .when(userStorage.findAll())
                .thenReturn(List.of(user));

        List<UserDto> users = userService.getUsers();
        UserDto userDtoOutgoing = users.get(0);

        assertThat(1, equalTo(users.size()));
        assertThat(user.getId(), equalTo(userDtoOutgoing.getId()));
        assertThat(user.getName(), equalTo(userDtoOutgoing.getName()));
        assertThat(user.getEmail(), equalTo(userDtoOutgoing.getEmail()));
    }

    @Test
    public void shouldAddUser() {
        Mockito
                .when(userStorage.save(any(User.class)))
                .then(returnsFirstArg());

        UserDto userDtoOutgoing = userService.addUser(userToDto(user));

        assertThat(user.getId(), equalTo(userDtoOutgoing.getId()));
        assertThat(user.getName(), equalTo(userDtoOutgoing.getName()));
        assertThat(user.getEmail(), equalTo(userDtoOutgoing.getEmail()));
    }

    @Test
    public void shouldNotAddUserWhenEmailNotUnique() {
        Mockito
                .when(userStorage.save(any(User.class)))
                .thenThrow(DataIntegrityViolationException.class);

        UserAlreadyExistsException e = Assertions.assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.addUser(userToDto(user))
        );

        assertEquals("Пользователь с email 'user1@email.com' уже существует", e.getMessage());
    }

    @Test
    public void shouldUpdateUser() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(userStorage.save(any(User.class)))
                .then(returnsFirstArg());

        UserDto userDtoOutgoing = userService.updateUser(userToDto(user));

        assertThat(user.getId(), equalTo(userDtoOutgoing.getId()));
        assertThat(user.getName(), equalTo(userDtoOutgoing.getName()));
        assertThat(user.getEmail(), equalTo(userDtoOutgoing.getEmail()));
    }

    @Test
    public void shouldNotUpdateUserWhenUserNotFound() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.empty());

        UserNotFoundException e = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser(userToDto(user))
        );

        assertEquals("Пользователь с id=1 не найден", e.getMessage());
    }

    @Test
    public void shouldUpdateUserWhenEmailNotUnique() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(userStorage.save(any(User.class)))
                .thenThrow(DataIntegrityViolationException.class);

        UserAlreadyExistsException e = Assertions.assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.updateUser(userToDto(user))
        );

        assertEquals("Пользователь с email 'user1@email.com' уже существует", e.getMessage());
    }

    @Test
    public void deleteUser() {
        userService.deleteUser(1);

        Mockito.verify(userStorage).deleteById(anyInt());
    }
}
