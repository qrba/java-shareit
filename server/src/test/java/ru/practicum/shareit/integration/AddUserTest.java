package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AddUserTest {
    private final EntityManager em;
    private final UserService userService;
    private final UserDto userDto = new UserDto(null, "user1", "user1@email.com");

    @Test
    public void shouldAddUser() {
        userService.addUser(userDto);
        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void shouldNotAddUserWhenEmailNotUnique() {
        UserAlreadyExistsException e = Assertions.assertThrows(
                UserAlreadyExistsException.class,
                () -> {
                    userService.addUser(userDto);
                    userService.addUser(userDto);
                }
        );

        assertThat(e.getMessage(), equalTo("Пользователь с email 'user1@email.com' уже существует"));
    }
}
