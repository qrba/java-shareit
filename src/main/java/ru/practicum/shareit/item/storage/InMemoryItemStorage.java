package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private int idCounter = 1;
    private final Map<Integer, Item> items;

    @Override
    public Item getItemById(int itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsByUserId(int userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item addItem(Item item) {
        item.setId(idCounter);
        idCounter++;
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void deleteItem(int itemId) {
        items.remove(itemId);
    }

    @Override
    public List<Item> findItems(String text) {
        return items.values().stream()
                .filter(
                        item -> (item.getIsAvailable()) &&
                                ((item.getName().toLowerCase().contains(text) ||
                                item.getDescription().toLowerCase().contains(text)))
                )
                .collect(Collectors.toList());
    }
}