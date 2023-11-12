package ru.practicum.shareit.itemrequest.model;

import lombok.Data;
import ru.practicum.shareit.item.model.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {
    private final Integer id;
    private final String description;
    private final LocalDateTime created;
    private final List<ItemDto> items;
}