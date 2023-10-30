package ru.practicum.shareit.itemrequest.model;

import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.model.ItemMapper.itemToDto;

public class ItemRequestMapper {
    public static ItemRequestDto itemRequestToDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                (itemRequest.getItems() == null) ?
                        Collections.emptyList() :
                        itemRequest.getItems().stream()
                        .map(item -> itemToDto(item, null, null, null))
                        .collect(Collectors.toList())
        );
    }

    public static ItemRequest itemRequestFromDto(ItemRequestDto itemRequestDto, User requestor) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                requestor,
                LocalDateTime.now(),
                null
        );
    }
}
