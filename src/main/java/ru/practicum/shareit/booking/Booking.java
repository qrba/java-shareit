package ru.practicum.shareit.booking;

import lombok.Data;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private int id;
    private final Long start;
    private final Long end;
    private final Integer item;
    private final Integer booker;
    private final BookingStatus status;
}
