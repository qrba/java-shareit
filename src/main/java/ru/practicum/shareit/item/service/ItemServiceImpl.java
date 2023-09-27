package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.WrongItemOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public Item getItemById(int itemId) {
        Item item = itemStorage.getItemById(itemId);
        if (item == null) throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
        log.info("Запрошена вещь {}", item);
        return item;
    }

    @Override
    public List<Item> getItemsByUserId(int userId) {
        if (userStorage.getUserById(userId) == null)
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        log.info("Запрошен список вещей пользователя с id={}", userId);
        return itemStorage.getItemsByUserId(userId);
    }

    @Override
    public Item addItem(Item item) {
        int userId = item.getOwner();
        if (userStorage.getUserById(userId) == null)
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        Item addedItem = itemStorage.addItem(item);
        log.info("Добавлена вещь {}", addedItem);
        return addedItem;
    }

    @Override
    public Item updateItem(Item item) {
        int userId = item.getOwner();
        int itemId = item.getId();
        userStorage.getUserById(userId);
        Item oldItem = getItemById(itemId);
        if (oldItem.getOwner() != userId)
            throw new WrongItemOwnerException("Пользователь с id=" + userId + " не владеет вещью с id=" + itemId);
        Item updatedItem = itemStorage.updateItem(item);
        log.info("Обновлена вещь {}", updatedItem);
        return updatedItem;
    }

    @Override
    public void deleteItem(int userId, int itemId) {
        if (userStorage.getUserById(userId) == null)
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        itemStorage.deleteItem(itemId);
        log.info("Удалена вещь c id={}", itemId);
    }

    @Override
    public List<Item> findItems(String text) {
        if (text.isBlank()) return Collections.emptyList();
        log.info("Запрошен поиск по тексту '{}'", text);
        return itemStorage.findItems(text.toLowerCase());
    }
}