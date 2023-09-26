package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.ItemDto;

import java.util.List;

public interface ItemStorage {
    ItemDto getItemById(int itemId);

    List<ItemDto> getItemsByUserId(int userId);

    ItemDto addItem(int userId, ItemDto itemDto);

    ItemDto updateItem(int userId, int itemId, ItemDto itemDto);

    void deleteItem(int itemId);

    List<ItemDto> findItems(String text);
}