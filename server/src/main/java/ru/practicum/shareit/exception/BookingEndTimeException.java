package ru.practicum.shareit.exception;

public class BookingEndTimeException extends RuntimeException {
    public BookingEndTimeException(String message) {
        super(message);
    }
}
