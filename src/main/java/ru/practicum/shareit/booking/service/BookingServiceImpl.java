package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDtoDefault;
import ru.practicum.shareit.booking.model.BookingDtoOutgoing;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
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
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingMapper.bookingFromDto;
import static ru.practicum.shareit.booking.model.BookingMapper.bookingToDtoOutgoing;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    public BookingDtoOutgoing addBooking(BookingDtoDefault bookingDtoDefault) {
        int userId = bookingDtoDefault.getBookerId();
        Optional<User> userOptional = userStorage.findById(userId);
        if (userOptional.isEmpty()) throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        int itemId = bookingDtoDefault.getItemId();
        Optional<Item> itemOptional = itemStorage.findById(itemId);
        if (itemOptional.isEmpty()) throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
        Item item = itemOptional.get();
        if (item.getOwner().getId() == userId)
            throw new BookingNotFoundException("Пользователь не может забронировать свою вещь");
        if (!item.getIsAvailable()) throw new ItemUnavailableException("Вещь с id=" + itemId + " не доступна");
        if (!bookingDtoDefault.getEnd().isAfter(bookingDtoDefault.getStart()))
            throw new BookingEndTimeException("Момент окончания бронирования должен быть позже начала");

        Booking booking = bookingStorage.save(
                bookingFromDto(bookingDtoDefault, userOptional.get(), item)
        );
        log.info("Добавлено бронирование {}", booking);
        return bookingToDtoOutgoing(booking);
    }

    @Override
    public BookingDtoOutgoing approveBooking(int userId, int bookingId, boolean approved) {
        Optional<Booking> bookingOptional = bookingStorage.findById(bookingId);
        if (bookingOptional.isEmpty())
            throw new BookingNotFoundException("Бронирование с id=" + bookingId + " не найдено");
        Booking booking = bookingOptional.get();
        if (booking.getItem().getOwner().getId() != userId) throw new BookingNotFoundException(
                    "У пользователя с id=" + userId + " не обнаружено бронирований с id=" + bookingId
            );
        if (!booking.getStatus().equals(BookingStatus.WAITING))
            throw new BookingStatusException("Статус бронирования не является 'WAITING'");
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            log.info("Пользователь с id={} подтвердил бронирование с id={}", userId, bookingId);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            log.info("Пользователь с id={} отклонил бронирование с id={}", userId, bookingId);
        }
        return bookingToDtoOutgoing(bookingStorage.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoOutgoing getById(int userId, int bookingId) {
        Optional<Booking> bookingOptional = bookingStorage.findById(bookingId);
        if (bookingOptional.isEmpty())
            throw new BookingNotFoundException("Бронирование с id=" + bookingId + " не найдено");
        Booking booking = bookingOptional.get();

        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId)
            throw new BookingNotFoundException(
                    "У пользователя с id=" + userId + " не обнаружено бронирований с id=" + bookingId
            );
        log.info("Пользователь с id={} запросил бронирование с id={}", userId, bookingId);
        return bookingToDtoOutgoing(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOutgoing> getUserBookings(int userId, String stateString) {
        BookingState state;
        try {
            state = BookingState.valueOf(stateString);
        } catch (IllegalArgumentException e) {
            throw new BookingStateException("Unknown state: " + stateString);
        }
        if (!userStorage.existsById(userId))
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        log.info("Пользователь с id={} запросил список своих бронирований со статусом {}", userId, state);
        List<Booking> bookings;

        switch (state) {
            case FUTURE:
                bookings = bookingStorage.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case CURRENT:
                LocalDateTime now = LocalDateTime.now();
                bookings = bookingStorage.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            case PAST:
                bookings = bookingStorage.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingStorage.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingStorage.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = bookingStorage.findByBookerIdOrderByStartDesc(userId);
        }

        return bookings.stream()
                .map(BookingMapper::bookingToDtoOutgoing)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOutgoing> getOwnerBookings(int userId, String stateString) {
        BookingState state;
        try {
            state = BookingState.valueOf(stateString);
        } catch (IllegalArgumentException e) {
            throw new BookingStateException("Unknown state: " + stateString);
        }
        if (!userStorage.existsById(userId))
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        log.info("Пользователь с id={} запросил список бронирований своих вещей со статусом {}", userId, state);
        List<Booking> bookings;

        switch (state) {
            case FUTURE:
                bookings = bookingStorage.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case CURRENT:
                LocalDateTime now = LocalDateTime.now();
                bookings = bookingStorage.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            case PAST:
                bookings = bookingStorage.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingStorage.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingStorage.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = bookingStorage.findByItemOwnerIdOrderByStartDesc(userId);
        }

        return bookings.stream()
                .map(BookingMapper::bookingToDtoOutgoing)
                .collect(Collectors.toList());
    }
}
