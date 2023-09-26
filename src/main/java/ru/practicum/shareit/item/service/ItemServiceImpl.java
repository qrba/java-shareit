package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto getItemById(int itemId) {
        return itemStorage.getItemById(itemId);
    }

    @Override
    public List<ItemDto> getItemsByUserId(int userId) {
        return itemStorage.getItemsByUserId(userId);
    }

    @Override
    public ItemDto addItem(int userId, ItemDto itemDto) {
        userStorage.getUserById(userId);
        return itemStorage.addItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, ItemDto itemDto) {
        userStorage.getUserById(userId);
        return itemStorage.updateItem(userId, itemId, itemDto);
    }

    @Override
    public void deleteItem(int itemId) {
        itemStorage.deleteItem(itemId);
    }

    @Override
    public List<ItemDto> findItems(String text) {
        return itemStorage.findItems(text);
    }
}