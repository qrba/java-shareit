package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private int idCounter = 1;
    private final Map<Integer, User> users;

    @Override
    public User getUserById(int userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        checkForAnEmail(user.getEmail());
        user.setId(idCounter);
        idCounter++;
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        User oldUser = users.get(user.getId());
        String emailUpdate = user.getEmail();
        String nameUpdate = user.getName();
        if (emailUpdate != null && !emailUpdate.isBlank() && !emailUpdate.contentEquals(oldUser.getEmail())) {
            checkForAnEmail(emailUpdate);
            oldUser.setEmail(emailUpdate);
        }
        String name = nameUpdate != null
                && !nameUpdate.isBlank()
                ? nameUpdate : oldUser.getName();
        oldUser.setName(name);
        return oldUser;
    }

    @Override
    public void deleteUser(int userId) {
        users.remove(userId);
    }

    private void checkForAnEmail(String email) {
        if (users.values().stream()
                .anyMatch(
                        user -> user.getEmail().equals(email)
                )
        ) throw new UserAlreadyExistsException("Пользователь с email '" + email + "' уже существует");
    }
}