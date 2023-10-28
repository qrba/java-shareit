package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.dto.BookingDtoDefault;
import ru.practicum.shareit.booking.model.dto.BookingDtoOutgoing;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BookingStateException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class GetUserBookingsTest {
    private final EntityManager em;
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    public void shouldGetUserBookings() {
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
        TypedQuery<User> queryUser = em.createQuery("select u from User u where u.email = :email", User.class);
        User user1 = queryUser.setParameter("email", userDto.getEmail()).getSingleResult();
        int user1Id = user1.getId();
        itemService.addItem(user1Id, itemDto);
        userDto = new UserDto(null, "user2", "user2@email.com");
        userService.addUser(userDto);
        queryUser = em.createQuery("select u from User u where u.email = :email", User.class);
        User user2 = queryUser.setParameter("email", userDto.getEmail()).getSingleResult();
        int user2Id = user2.getId();
        TypedQuery<Item> queryItem = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item item = queryItem.setParameter("name", itemDto.getName()).getSingleResult();
        BookingDtoDefault bookingDtoDefault = new BookingDtoDefault(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item.getId(),
                user2Id,
                BookingStatus.WAITING);
        bookingService.addBooking(bookingDtoDefault);
        List<BookingDtoOutgoing> bookings = bookingService.getUserBookings(user2Id, "ALL", 0, 5);
        BookingDtoOutgoing bookingDtoOutgoing = bookings.get(0);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookingDtoOutgoing.getId(), notNullValue());
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(bookingDtoDefault.getStatus()));
    }

    @Test
    public void shouldNotGetUserBookingsWhenUnknownState() {
        BookingStateException e = Assertions.assertThrows(
                BookingStateException.class,
                () -> bookingService.getUserBookings(1, "Test", 0, 5)
        );

        assertThat(e.getMessage(), equalTo("Unknown state: Test"));
    }

    @Test
    public void shouldNotGetUserBookingsWhenUserNotFound() {
        UserNotFoundException e = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> bookingService.getUserBookings(1, "ALL", 0, 5)
        );

        assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
    }
}
