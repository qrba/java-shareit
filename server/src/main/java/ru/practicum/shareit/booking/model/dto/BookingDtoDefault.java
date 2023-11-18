package ru.practicum.shareit.booking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDtoDefault {
    private Integer id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Integer itemId;
    private Integer bookerId;
    private BookingStatus status;
}