package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item getItemById(int itemId);

    List<Item> getItemsByUserId(int userId);

    Item addItem(Item item);

    Item updateItem(Item item);

    void deleteItem(int itemId);

    List<Item> findItems(String text);
}