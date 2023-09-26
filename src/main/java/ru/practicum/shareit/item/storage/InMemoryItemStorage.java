package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.WrongItemOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.model.ItemMapper.dtoToItem;
import static ru.practicum.shareit.item.model.ItemMapper.itemToDto;

@Repository
@RequiredArgsConstructor
@Slf4j
public class InMemoryItemStorage implements ItemStorage {
    private int idCounter = 1;
    private final Map<Integer, Item> items;

    @Override
    public ItemDto getItemById(int itemId) {
        Item item = items.get(itemId);
        if (item == null) throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
        log.info("Запрошена вещь {}", item);
        return itemToDto(items.get(itemId));
    }

    @Override
    public List<ItemDto> getItemsByUserId(int userId) {
        log.info("Запрошен список вещей пользователя с id={}", userId);
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto addItem(int userId, ItemDto itemDto) {
        Item item = dtoToItem(userId, itemDto);
        item.setId(idCounter);
        idCounter++;
        log.info("Добавлена вещь {}", item);
        items.put(item.getId(), item);
        return itemToDto(item);
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, ItemDto itemDto) {
        Item item = items.get(itemId);
        if (item == null) throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
        if (item.getOwner() != userId)
            throw new WrongItemOwnerException("Пользователь с id=" + userId + " не владеет вещью с id=" + itemId);
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean isAvailable = itemDto.getIsAvailable();
        if (name != null && !name.isBlank()) item.setName(name);
        if (description != null && !description.isBlank()) item.setDescription(description);
        if (isAvailable != null) item.setIsAvailable(isAvailable);
        log.info("Обновлена вещь {}", item);
        return itemToDto(item);
    }

    @Override
    public void deleteItem(int itemId) {
        log.info("Удалена вещь {}", items.remove(itemId));
    }

    @Override
    public List<ItemDto> findItems(String text) {
        log.info("Запрошен поиск по тексту '{}'", text);
        if (text.isBlank()) return Collections.emptyList();
        String lowerCaseText = text.toLowerCase();
        return items.values().stream()
                .filter(
                        item -> (item.getIsAvailable()) &&
                                ((item.getName().toLowerCase().contains(lowerCaseText) ||
                                item.getDescription().toLowerCase().contains(lowerCaseText)))
                )
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }
}