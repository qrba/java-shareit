package ru.practicum.shareit.itemrequest.service;

import ru.practicum.shareit.itemrequest.model.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(int userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getItemRequestsByUserId(int userId);

    List<ItemRequestDto> getAllItemRequests(int userId, int from, int size);

    ItemRequestDto getItemRequestById(int userId, int requestId);
}