package ru.practicum.shareit.item.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.dto.BookingDtoDefault;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {
    private Integer id;
    private final String name;
    private final String description;
    @JsonProperty("available")
    private final Boolean isAvailable;
    private final Integer requestId;
    private final BookingDtoDefault lastBooking;
    private final BookingDtoDefault nextBooking;
    private final List<CommentDto> comments;
}