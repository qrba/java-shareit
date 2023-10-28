package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.dto.BookingDtoDefault;
import ru.practicum.shareit.booking.model.dto.BookingDtoOutgoing;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.BookingEndTimeException;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.BookingStateException;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static ru.practicum.shareit.booking.model.BookingMapper.bookingFromDto;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private ItemStorage itemStorage;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Item item;
    private BookingDtoDefault bookingDtoDefault;

    @BeforeEach
    public void setUp() {
        user = new User(1, "user1", "user1@email.com");

        item = new Item(
                1,
                "Item1",
                "Test item 1",
                true,
                new User(2, "user2", "user2@email.com"),
                null
        );

        bookingDtoDefault = new BookingDtoDefault(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1,
                1,
                BookingStatus.WAITING
        );
    }

    @Test
    public void shouldAddBooking() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemStorage.findById(anyInt()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(bookingStorage.save(any(Booking.class)))
                .then(returnsFirstArg());

        BookingDtoOutgoing bookingDtoOutgoing = bookingService.addBooking(bookingDtoDefault);

        assertThat(bookingDtoDefault.getId(), equalTo(bookingDtoOutgoing.getId()));
        assertThat(bookingDtoDefault.getStart(), equalTo(bookingDtoOutgoing.getStart()));
        assertThat(bookingDtoDefault.getEnd(), equalTo(bookingDtoOutgoing.getEnd()));
        assertThat(bookingDtoDefault.getItemId(), equalTo(bookingDtoOutgoing.getItem().getId()));
        assertThat(bookingDtoDefault.getBookerId(), equalTo(bookingDtoOutgoing.getBooker().getId()));
        assertThat(bookingDtoDefault.getStatus(), equalTo(bookingDtoOutgoing.getStatus()));
    }

    @Test
    public void shouldNotAddBookingWhenUserNotFound() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.empty());

        UserNotFoundException e = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> bookingService.addBooking(bookingDtoDefault)
        );

        assertEquals("Пользователь с id=1 не найден", e.getMessage());
    }

    @Test
    public void shouldNotAddBookingWhenItemNotFound() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemStorage.findById(anyInt()))
                .thenReturn(Optional.empty());

        ItemNotFoundException e = Assertions.assertThrows(
                ItemNotFoundException.class,
                () -> bookingService.addBooking(bookingDtoDefault)
        );

        assertEquals("Вещь с id=1 не найдена", e.getMessage());
    }

    @Test
    public void shouldNotAddBookingWhenOwner() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.of(user));

        item = new Item(
                1,
                "Item1",
                "Test item 1",
                true,
                user,
                null
        );
        Mockito
                .when(itemStorage.findById(anyInt()))
                .thenReturn(Optional.of(item));

        BookingNotFoundException e = Assertions.assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.addBooking(bookingDtoDefault)
        );

        assertEquals("Пользователь не может забронировать свою вещь", e.getMessage());
    }

    @Test
    public void shouldNotAddBookingWhenItemUnavailable() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.of(user));

        item = new Item(
                1,
                "Item1",
                "Test item 1",
                false,
                new User(2, "user2", "user2@email.com"),
                null
        );
        Mockito
                .when(itemStorage.findById(anyInt()))
                .thenReturn(Optional.of(item));

        ItemUnavailableException e = Assertions.assertThrows(
                ItemUnavailableException.class,
                () -> bookingService.addBooking(bookingDtoDefault)
        );

        assertEquals("Вещь с id=1 не доступна", e.getMessage());
    }

    @Test
    public void shouldNotAddBookingWhenEndBeforeStart() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemStorage.findById(anyInt()))
                .thenReturn(Optional.of(item));
        bookingDtoDefault = new BookingDtoDefault(
                1,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1),
                1,
                1,
                BookingStatus.WAITING
        );

        BookingEndTimeException e = Assertions.assertThrows(
                BookingEndTimeException.class,
                () -> bookingService.addBooking(bookingDtoDefault)
        );

        assertEquals("Момент окончания бронирования должен быть позже начала", e.getMessage());
    }

    @Test
    public void shouldApproveBooking() {
        Mockito
                .when(bookingStorage.findByIdAndItemOwnerId(anyInt(), anyInt()))
                .thenReturn(Optional.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));
        Mockito
                .when(bookingStorage.save(any(Booking.class)))
                .then(returnsFirstArg());

        BookingDtoOutgoing bookingDtoOutgoing = bookingService.approveBooking(2, 1, true);

        assertThat(bookingDtoDefault.getId(), equalTo(bookingDtoOutgoing.getId()));
        assertThat(bookingDtoDefault.getStart(), equalTo(bookingDtoOutgoing.getStart()));
        assertThat(bookingDtoDefault.getEnd(), equalTo(bookingDtoOutgoing.getEnd()));
        assertThat(bookingDtoDefault.getItemId(), equalTo(bookingDtoOutgoing.getItem().getId()));
        assertThat(bookingDtoDefault.getBookerId(), equalTo(bookingDtoOutgoing.getBooker().getId()));
        assertThat(BookingStatus.APPROVED, equalTo(bookingDtoOutgoing.getStatus()));
    }

    @Test
    public void shouldRejectBooking() {
        Mockito
                .when(bookingStorage.findByIdAndItemOwnerId(anyInt(), anyInt()))
                .thenReturn(Optional.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));
        Mockito
                .when(bookingStorage.save(any(Booking.class)))
                .then(returnsFirstArg());

        BookingDtoOutgoing bookingDtoOutgoing = bookingService.approveBooking(2, 1, false);

        assertThat(bookingDtoDefault.getId(), equalTo(bookingDtoOutgoing.getId()));
        assertThat(bookingDtoDefault.getStart(), equalTo(bookingDtoOutgoing.getStart()));
        assertThat(bookingDtoDefault.getEnd(), equalTo(bookingDtoOutgoing.getEnd()));
        assertThat(bookingDtoDefault.getItemId(), equalTo(bookingDtoOutgoing.getItem().getId()));
        assertThat(bookingDtoDefault.getBookerId(), equalTo(bookingDtoOutgoing.getBooker().getId()));
        assertThat(BookingStatus.REJECTED, equalTo(bookingDtoOutgoing.getStatus()));
    }

    @Test
    public void shouldNotApproveBookingWhenBookingNotFound() {
        Mockito
                .when(bookingStorage.findByIdAndItemOwnerId(anyInt(), anyInt()))
                .thenReturn(Optional.empty());

        BookingNotFoundException e = Assertions.assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.approveBooking(1, 1, true)
        );

        assertEquals("Бронирование с id=1 не найдено", e.getMessage());
    }

    @Test
    public void shouldNotApproveBookingWhenBookingStatusNotWaiting() {
        bookingDtoDefault = new BookingDtoDefault(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1,
                1,
                BookingStatus.CANCELED
        );
        Mockito
                .when(bookingStorage.findByIdAndItemOwnerId(anyInt(), anyInt()))
                .thenReturn(Optional.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));

        BookingStatusException e = Assertions.assertThrows(
                BookingStatusException.class,
                () -> bookingService.approveBooking(2, 1, true)
        );

        assertEquals("Статус бронирования не является 'WAITING'", e.getMessage());
    }

    @Test
    public void shouldGetById() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(bookingStorage.findById(anyInt()))
                .thenReturn(Optional.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));

        BookingDtoOutgoing bookingDtoOutgoing = bookingService.getById(1, 1);

        assertThat(bookingDtoDefault.getId(), equalTo(bookingDtoOutgoing.getId()));
        assertThat(bookingDtoDefault.getStart(), equalTo(bookingDtoOutgoing.getStart()));
        assertThat(bookingDtoDefault.getEnd(), equalTo(bookingDtoOutgoing.getEnd()));
        assertThat(bookingDtoDefault.getItemId(), equalTo(bookingDtoOutgoing.getItem().getId()));
        assertThat(bookingDtoDefault.getBookerId(), equalTo(bookingDtoOutgoing.getBooker().getId()));
        assertThat(bookingDtoDefault.getStatus(), equalTo(bookingDtoOutgoing.getStatus()));
    }

    @Test
    public void shouldNotGetByIdWhenUserNotFound() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(false);

        UserNotFoundException e = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> bookingService.getById(1, 1)
        );

        assertEquals("Пользователь с id=1 не найден", e.getMessage());
    }

    @Test
    public void shouldNotGetByIdWhenBookingNotFound() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(bookingStorage.findById(anyInt()))
                .thenReturn(Optional.empty());

        BookingNotFoundException e = Assertions.assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.getById(1, 1)
        );

        assertEquals("Бронирование с id=1 не найдено", e.getMessage());
    }

    @Test
    public void shouldNotGetByIdWhenUserNotOwnerOrBooker() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(bookingStorage.findById(anyInt()))
                .thenReturn(Optional.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));

        BookingNotFoundException e = Assertions.assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.getById(3, 1)
        );

        assertEquals("У пользователя с id=3 не обнаружено бронирований с id=1", e.getMessage());
    }

    @Test
    public void shouldGetUserBookings() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(
                        bookingStorage.findByBookerIdOrderByStartDesc(
                                anyInt(),
                                any(Pageable.class))
                )
                .thenReturn(List.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));

        List<BookingDtoOutgoing> bookings = bookingService.getUserBookings(1, "ALL", 0, 5);
        BookingDtoOutgoing bookingDtoOutgoing = bookings.get(0);

        assertThat(1, equalTo(bookings.size()));
        assertThat(bookingDtoDefault.getId(), equalTo(bookingDtoOutgoing.getId()));
        assertThat(bookingDtoDefault.getStart(), equalTo(bookingDtoOutgoing.getStart()));
        assertThat(bookingDtoDefault.getEnd(), equalTo(bookingDtoOutgoing.getEnd()));
        assertThat(bookingDtoDefault.getItemId(), equalTo(bookingDtoOutgoing.getItem().getId()));
        assertThat(bookingDtoDefault.getBookerId(), equalTo(bookingDtoOutgoing.getBooker().getId()));
        assertThat(bookingDtoDefault.getStatus(), equalTo(bookingDtoOutgoing.getStatus()));
    }

    @Test
    public void shouldNotGetUserBookingsWhenUnknownState() {
        BookingStateException e = Assertions.assertThrows(
                BookingStateException.class,
                () -> bookingService.getUserBookings(1, "Test", 0, 5)
        );

        assertEquals("Unknown state: Test", e.getMessage());
    }

    @Test
    public void shouldNotGetUserBookingsWhenUserNotFound() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(false);

        UserNotFoundException e = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> bookingService.getUserBookings(1, "ALL", 0, 5)
        );

        assertEquals("Пользователь с id=1 не найден", e.getMessage());
    }

    @Test
    public void shouldGetOwnerBookings() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(
                        bookingStorage.findByItemOwnerIdOrderByStartDesc(
                                anyInt(),
                                any(Pageable.class))
                )
                .thenReturn(List.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));

        List<BookingDtoOutgoing> bookings = bookingService.getOwnerBookings(2, "ALL", 0, 5);
        BookingDtoOutgoing bookingDtoOutgoing = bookings.get(0);

        assertThat(1, equalTo(bookings.size()));
        assertThat(bookingDtoDefault.getId(), equalTo(bookingDtoOutgoing.getId()));
        assertThat(bookingDtoDefault.getStart(), equalTo(bookingDtoOutgoing.getStart()));
        assertThat(bookingDtoDefault.getEnd(), equalTo(bookingDtoOutgoing.getEnd()));
        assertThat(bookingDtoDefault.getItemId(), equalTo(bookingDtoOutgoing.getItem().getId()));
        assertThat(bookingDtoDefault.getBookerId(), equalTo(bookingDtoOutgoing.getBooker().getId()));
        assertThat(bookingDtoDefault.getStatus(), equalTo(bookingDtoOutgoing.getStatus()));
    }

    @Test
    public void shouldNotGetOwnerBookingsWhenUnknownState() {
        BookingStateException e = Assertions.assertThrows(
                BookingStateException.class,
                () -> bookingService.getOwnerBookings(1, "Test", 0, 5)
        );

        assertEquals("Unknown state: Test", e.getMessage());
    }

    @Test
    public void shouldNotGetOwnerBookingsWhenUserNotFound() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(false);

        UserNotFoundException e = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> bookingService.getOwnerBookings(1, "ALL", 0, 5)
        );

        assertEquals("Пользователь с id=1 не найден", e.getMessage());
    }
}
