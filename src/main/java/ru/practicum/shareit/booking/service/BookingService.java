package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.dto.BookingDtoDefault;
import ru.practicum.shareit.booking.model.dto.BookingDtoOutgoing;

import java.util.List;

public interface BookingService {
    BookingDtoOutgoing addBooking(BookingDtoDefault bookingDtoDefault);

    BookingDtoOutgoing approveBooking(int userId, int bookingId, boolean approved);

    BookingDtoOutgoing getById(int userId, int bookingId);

    List<BookingDtoOutgoing> getUserBookings(int userId, String stateString, int from, int size);

    List<BookingDtoOutgoing> getOwnerBookings(int userId, String stateString, int from, int size);
}
