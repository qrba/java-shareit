package ru.practicum.shareit.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageTest {
    private final UserStorage userStorage;

    @Test
    void shouldAddUserAndGetUserById() {
        User user = User.builder()
                .id(1)
                .name("Test username")
                .email("test@email.com")
                .build();
        userStorage.addUser(user);
        User userById = userStorage.getUserById(1);

        assertEquals(user, userById);
    }

    @Test
    void shouldGetUsers() {
        User user = User.builder()
                .id(1)
                .name("Test username")
                .email("test@email.com")
                .build();
        userStorage.addUser(user);
        List<User> users = userStorage.getUsers();

        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
    }

    @Test
    void shouldUpdateUser() {
        User user = User.builder()
                .id(1)
                .name("Test username")
                .email("test@email.com")
                .build();
        userStorage.addUser(user);
        User userToUpdate = User.builder()
                .id(1)
                .name("Updated test username")
                .email("test@email.com")
                .build();
        userStorage.updateUser(userToUpdate);
        User updatedUser = userStorage.getUserById(1);

        assertEquals(userToUpdate, updatedUser);
    }

    @Test
    void shouldDeleteUser() {
        User user = User.builder()
                .id(1)
                .name("Test username")
                .email("test@email.com")
                .build();
        userStorage.addUser(user);
        userStorage.deleteUser(1);

        assertNull(userStorage.getUserById(1));
    }

    @Test
    void shouldCheckForAnEmail() {
        User user = User.builder()
                .id(1)
                .name("Test username")
                .email("test@email.com")
                .build();
        userStorage.addUser(user);

        assertTrue(userStorage.checkForAnEmail("test@email.com"));
        assertFalse(userStorage.checkForAnEmail("anothertest@email.com"));
    }
}
