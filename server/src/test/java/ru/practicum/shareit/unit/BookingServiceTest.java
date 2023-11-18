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

        assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDtoDefault.getId()));
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(bookingDtoDefault.getStatus()));
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

        assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
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

        assertThat(e.getMessage(), equalTo("Вещь с id=1 не найдена"));
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

        assertThat(e.getMessage(), equalTo("Пользователь не может забронировать свою вещь"));
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

        assertThat(e.getMessage(), equalTo("Вещь с id=1 не доступна"));
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

        assertThat(e.getMessage(), equalTo("Момент окончания бронирования должен быть позже начала"));
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

        assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDtoDefault.getId()));
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(BookingStatus.APPROVED));
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

        assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDtoDefault.getId()));
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(BookingStatus.REJECTED));
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

        assertThat(e.getMessage(), equalTo("Бронирование с id=1 не найдено"));
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

        assertThat(e.getMessage(), equalTo("Статус бронирования не является 'WAITING'"));
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

        assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDtoDefault.getId()));
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(bookingDtoDefault.getStatus()));
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

        assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
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

        assertThat(e.getMessage(), equalTo("Бронирование с id=1 не найдено"));
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

        assertThat(e.getMessage(), equalTo("У пользователя с id=3 не обнаружено бронирований с id=1"));
    }

    @Test
    public void shouldGetAllUserBookings() {
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

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDtoDefault.getId()));
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(bookingDtoDefault.getStatus()));
    }

    @Test
    public void shouldGetWaitingUserBookings() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(
                        bookingStorage.findByBookerIdAndStatusOrderByStartDesc(
                                anyInt(),
                                any(BookingStatus.class),
                                any(Pageable.class))
                )
                .thenReturn(List.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));

        List<BookingDtoOutgoing> bookings = bookingService.getUserBookings(1, "WAITING", 0, 5);
        BookingDtoOutgoing bookingDtoOutgoing = bookings.get(0);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDtoDefault.getId()));
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(bookingDtoDefault.getStatus()));
    }

    @Test
    public void shouldGetFutureUserBookings() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(
                        bookingStorage.findByBookerIdAndStartAfterOrderByStartDesc(
                                anyInt(),
                                any(LocalDateTime.class),
                                any(Pageable.class))
                )
                .thenReturn(List.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));

        List<BookingDtoOutgoing> bookings = bookingService.getUserBookings(1, "FUTURE", 0, 5);
        BookingDtoOutgoing bookingDtoOutgoing = bookings.get(0);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDtoDefault.getId()));
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(bookingDtoDefault.getStatus()));
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

        assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
    }

    @Test
    public void shouldGetAllOwnerBookings() {
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

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDtoDefault.getId()));
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(bookingDtoDefault.getStatus()));
    }

    @Test
    public void shouldGetWaitingOwnerBookings() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(
                        bookingStorage.findByItemOwnerIdAndStatusOrderByStartDesc(
                                anyInt(),
                                any(BookingStatus.class),
                                any(Pageable.class))
                )
                .thenReturn(List.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));

        List<BookingDtoOutgoing> bookings = bookingService.getOwnerBookings(1, "WAITING", 0, 5);
        BookingDtoOutgoing bookingDtoOutgoing = bookings.get(0);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDtoDefault.getId()));
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(bookingDtoDefault.getStatus()));
    }

    @Test
    public void shouldGetFutureOwnerBookings() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(
                        bookingStorage.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                                anyInt(),
                                any(LocalDateTime.class),
                                any(Pageable.class))
                )
                .thenReturn(List.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));

        List<BookingDtoOutgoing> bookings = bookingService.getOwnerBookings(1, "FUTURE", 0, 5);
        BookingDtoOutgoing bookingDtoOutgoing = bookings.get(0);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDtoDefault.getId()));
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(bookingDtoDefault.getStatus()));
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

        assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
    }
}