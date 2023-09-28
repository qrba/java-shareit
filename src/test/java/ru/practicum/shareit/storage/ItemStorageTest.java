package ru.practicum.shareit.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemStorageTest {
    private final ItemStorage itemStorage;

    @Test
    void shouldAddItemAndGetItemById() {
        Item item = Item.builder()
                .id(1)
                .name("Test item name")
                .description("Test item is for doing tests")
                .isAvailable(true)
                .owner(1)
                .requestId(0)
                .build();
        itemStorage.addItem(item);
        Item itemById = itemStorage.getItemById(1);

        assertEquals(item, itemById);
    }

    @Test
    void shouldGetItemsByUserId() {
        Item item = Item.builder()
                .id(1)
                .name("Test item name")
                .description("Test item is for doing tests")
                .isAvailable(true)
                .owner(1)
                .requestId(0)
                .build();
        itemStorage.addItem(item);
        List<Item> items = itemStorage.getItemsByUserId(1);

        assertEquals(1, items.size());
        assertEquals(item, items.get(0));
    }

    @Test
    void shouldUpdateItem() {
        Item item = Item.builder()
                .id(1)
                .name("Test item name")
                .description("Test item is for doing tests")
                .isAvailable(true)
                .owner(1)
                .requestId(0)
                .build();
        itemStorage.addItem(item);
        Item itemToUpdate = Item.builder()
                .id(1)
                .name("Updated item name")
                .description("Test item is for doing tests")
                .isAvailable(false)
                .owner(1)
                .requestId(0)
                .build();
        itemStorage.updateItem(itemToUpdate);
        Item updatedItem = itemStorage.getItemById(1);

        assertEquals(itemToUpdate, updatedItem);
    }

    @Test
    void shouldDeleteItem() {
        Item item = Item.builder()
                .id(1)
                .name("Test item name")
                .description("Test item is for doing tests")
                .isAvailable(true)
                .owner(1)
                .requestId(0)
                .build();
        itemStorage.addItem(item);
        itemStorage.deleteItem(1);
        Item itemById = itemStorage.getItemById(1);

        assertNull(itemById);
    }

    @Test
    void shouldFindItems() {
        Item item = Item.builder()
                .id(1)
                .name("Test item name")
                .description("Test item is for doing tests")
                .isAvailable(true)
                .owner(1)
                .requestId(0)
                .build();
        itemStorage.addItem(item);
        List<Item> items = itemStorage.findItems("doing");

        assertEquals(1, items.size());
        assertEquals(item, items.get(0));
    }
}
