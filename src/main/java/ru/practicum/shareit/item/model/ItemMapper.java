package ru.practicum.shareit.item.model;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static ru.practicum.shareit.booking.model.BookingMapper.bookingToDtoDefault;

public class ItemMapper {
    public static ItemDto itemToDto(Item item, Booking last, Booking next, List<CommentDto> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getRequestId(),
                last == null ? null : bookingToDtoDefault(last),
                next == null ? null : bookingToDtoDefault(next),
                comments
        );
    }

    public static Item itemFromDto(ItemDto itemDto, User user) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(user)
                .isAvailable(itemDto.getIsAvailable())
                .requestId(itemDto.getRequestId())
                .build();
    }
}
