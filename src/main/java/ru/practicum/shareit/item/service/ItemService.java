package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(int userId, int itemId);

    List<ItemDto> getItemsByUserId(int userId);

    ItemDto addItem(int userId, ItemDto itemDto);

    ItemDto updateItem(int userId, ItemDto itemDto);

    void deleteItem(int userId, int itemId);

    List<ItemDto> findItems(String text);

    CommentDto addComment(int userId, int itemId, CommentDto commentDto);
}