package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class GetItemsByUserIdTest {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    public void shouldGetItemsByUserId() {
        ItemDto itemDto = new ItemDto(
                null,
                "Test name",
                "Test description",
                true,
                null,
                null,
                null,
                null
        );
        UserDto userDto = new UserDto(null, "user1", "user1@email.com");
        userService.addUser(userDto);
        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();
        int userId = user.getId();
        itemService.addItem(userId, itemDto);
        List<ItemDto> items = itemService.getItemsByUserId(userId, 0, 5);
        ItemDto itemDtoOutgoing = items.get(0);

        assertThat(items.size(), equalTo(1));
        assertThat(itemDtoOutgoing.getId(), notNullValue());
        assertThat(itemDtoOutgoing.getName(), equalTo(itemDto.getName()));
        assertThat(itemDtoOutgoing.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(itemDtoOutgoing.getIsAvailable(), equalTo(itemDto.getIsAvailable()));
        assertThat(itemDtoOutgoing.getRequestId(), nullValue());
        assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
        assertThat(itemDtoOutgoing.getNextBooking(), nullValue());
        assertThat(itemDtoOutgoing.getComments(), equalTo(Collections.emptyList()));
    }

    @Test
    public void shouldNotGetItemsByUserIdWhenUserNotFound() {
        UserNotFoundException e = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> itemService.getItemsByUserId(1, 0, 5)
        );

        assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
    }
}
