package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.WrongItemOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.model.ItemMapper.dtoToItem;
import static ru.practicum.shareit.item.model.ItemMapper.itemToDto;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto getItemById(int itemId) {
        Item item = itemStorage.getItemById(itemId);
        if (item == null) throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
        log.info("Запрошена вещь {}", item);
        return itemToDto(item);
    }

    @Override
    public List<ItemDto> getItemsByUserId(int userId) {
        if (userStorage.getUserById(userId) == null)
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        log.info("Запрошен список вещей пользователя с id={}", userId);
        return itemStorage.getItemsByUserId(userId).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto addItem(int userId, ItemDto itemDto) {
        if (userStorage.getUserById(userId) == null)
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        Item addedItem = itemStorage.addItem(
                dtoToItem(userId, itemDto)
        );
        log.info("Добавлена вещь {}", addedItem);
        return itemToDto(addedItem);
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, ItemDto itemDto) {
        userStorage.getUserById(userId);
        Item oldItem = itemStorage.getItemById(itemId);
        if (oldItem == null) throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
        if (oldItem.getOwner() != userId)
            throw new WrongItemOwnerException("Пользователь с id=" + userId + " не владеет вещью с id=" + itemId);
        Item item = dtoToItem(userId, itemDto);
        item.setId(itemId);

        String name = item.getName();
        String description = item.getDescription();
        if (name == null || name.isBlank()) item.setName(oldItem.getName());
        if (description == null || description.isBlank()) item.setDescription(oldItem.getDescription());
        if (item.getIsAvailable() == null) item.setIsAvailable(oldItem.getIsAvailable());

        Item updatedItem = itemStorage.updateItem(item);
        log.info("Обновлена вещь {}", updatedItem);
        return itemToDto(updatedItem);
    }

    @Override
    public void deleteItem(int userId, int itemId) {
        if (userStorage.getUserById(userId) == null)
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        itemStorage.deleteItem(itemId);
        log.info("Удалена вещь c id={}", itemId);
    }

    @Override
    public List<ItemDto> findItems(String text) {
        if (text.isBlank()) return Collections.emptyList();
        log.info("Запрошен поиск по тексту '{}'", text);
        return itemStorage.findItems(text.toLowerCase()).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }
}