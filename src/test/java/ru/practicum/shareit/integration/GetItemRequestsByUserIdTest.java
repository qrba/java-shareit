package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.itemrequest.model.ItemRequestDto;
import ru.practicum.shareit.itemrequest.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class GetItemRequestsByUserIdTest {
    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @Test
    public void shouldGetItemRequestsByUserId() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                null,
                "Test description",
                LocalDateTime.now(),
                null
        );
        UserDto userDto = new UserDto(null, "user1", "user1@email.com");
        userService.addUser(userDto);
        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();
        int userId = user.getId();
        itemRequestService.addItemRequest(userId, itemRequestDto);
        List<ItemRequestDto> itemRequests = itemRequestService.getItemRequestsByUserId(userId);
        ItemRequestDto itemRequestDtoOutgoing = itemRequests.get(0);

        assertThat(1, equalTo(itemRequests.size()));
        assertThat(itemRequestDtoOutgoing.getId(), notNullValue());
        assertThat(itemRequestDtoOutgoing.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequestDtoOutgoing.getItems(), equalTo(Collections.emptyList()));
    }

    @Test
    public void shouldNotGetItemRequestsByUserIdWhenUserNotFound() {
        UserNotFoundException e = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.getItemRequestsByUserId(1)
        );

        assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
    }
}
