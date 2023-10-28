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

        assertThat(userDtoOutgoing.getId(), equalTo(user.getId()));
        assertThat(userDtoOutgoing.getName(), equalTo(user.getName()));
        assertThat(userDtoOutgoing.getEmail(), equalTo(user.getEmail()));
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

        assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
    }

    @Test
    public void shouldGetUsers() {
        Mockito
                .when(userStorage.findAll())
                .thenReturn(List.of(user));

        List<UserDto> users = userService.getUsers();
        UserDto userDtoOutgoing = users.get(0);

        assertThat(users.size(), equalTo(1));
        assertThat(userDtoOutgoing.getId(), equalTo(user.getId()));
        assertThat(userDtoOutgoing.getName(), equalTo(user.getName()));
        assertThat(userDtoOutgoing.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void shouldAddUser() {
        Mockito
                .when(userStorage.save(any(User.class)))
                .then(returnsFirstArg());

        UserDto userDtoOutgoing = userService.addUser(userToDto(user));

        assertThat(userDtoOutgoing.getId(), equalTo(user.getId()));
        assertThat(userDtoOutgoing.getName(), equalTo(user.getName()));
        assertThat(userDtoOutgoing.getEmail(), equalTo(user.getEmail()));
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

        assertThat(e.getMessage(), equalTo("Пользователь с email 'user1@email.com' уже существует"));
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

        assertThat(userDtoOutgoing.getId(), equalTo(user.getId()));
        assertThat(userDtoOutgoing.getName(), equalTo(user.getName()));
        assertThat(userDtoOutgoing.getEmail(), equalTo(user.getEmail()));
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

        assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
    }

    @Test
    public void shouldNotUpdateUserWhenEmailNotUnique() {
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

        assertThat(e.getMessage(), equalTo("Пользователь с email 'user1@email.com' уже существует"));
    }

    @Test
    public void deleteUser() {
        userService.deleteUser(1);

        Mockito.verify(userStorage).deleteById(anyInt());
    }
}