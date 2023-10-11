package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.booking.model.dto.BookingDtoDefault;
import ru.practicum.shareit.booking.model.dto.BookingDtoOutgoing;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static ru.practicum.shareit.item.model.ItemMapper.itemToDto;
import static ru.practicum.shareit.user.model.UserMapper.userToDto;

public class BookingMapper {
    public static BookingDtoOutgoing bookingToDtoOutgoing(Booking booking) {
        return new BookingDtoOutgoing(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                itemToDto(booking.getItem(), null, null, null),
                userToDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static Booking bookingFromDto(BookingDtoDefault bookingDtoDefault, User booker, Item item) {
        return new Booking(
                bookingDtoDefault.getId(),
                bookingDtoDefault.getStart(),
                bookingDtoDefault.getEnd(),
                item,
                booker,
                bookingDtoDefault.getStatus()
        );
    }

    public static BookingDtoDefault bookingToDtoDefault(Booking booking) {
        return new BookingDtoDefault(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }
}
