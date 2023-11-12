package ru.practicum.shareit.unit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.user.UserClient;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserClientTest {
    private final UserClient userClient;
/*
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
    }*/
}