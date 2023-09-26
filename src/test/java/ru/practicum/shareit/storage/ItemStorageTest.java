package ru.practicum.shareit.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.WrongItemOwnerException;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemStorageTest {
    private final ItemStorage itemStorage;

    @Test
    void shouldAddItemAndGetItemById() {
        ItemDto itemDto = new ItemDto(1, "Test item name",
                "Test item is for doing tests", true, 0);
        itemStorage.addItem(1, itemDto);
        ItemDto itemById = itemStorage.getItemById(1);

        assertEquals(itemDto, itemById);
    }

    @Test
    void shouldNotGetItemByIdWhenIncorrectId() {
        ItemNotFoundException e = assertThrows(
                ItemNotFoundException.class,
                () -> itemStorage.getItemById(10)
        );

        assertEquals("Вещь с id=10 не найдена", e.getMessage());
    }

    @Test
    void shouldGetItemsByUserId() {
        ItemDto itemDto = new ItemDto(1, "Test item name",
                "Test item is for doing tests", true, 0);
        itemStorage.addItem(1, itemDto);
        List<ItemDto> items = itemStorage.getItemsByUserId(1);

        assertEquals(1, items.size());
        assertEquals(itemDto, items.get(0));
    }

    @Test
    void shouldUpdateItem() {
        ItemDto itemDto = new ItemDto(1, "Test item name",
                "Test item is for doing tests", true, 0);
        itemStorage.addItem(1, itemDto);
        ItemDto itemDtoToUpdate = new ItemDto(1, "Updated item name",
                "Test item is for doing tests", false, 0);
        itemStorage.updateItem(1, 1, itemDtoToUpdate);
        ItemDto updatedItemDto = itemStorage.getItemById(1);

        assertEquals(itemDtoToUpdate, updatedItemDto);
    }

    @Test
    void shouldNotUpdateItemWhenIncorrectOwner() {
        WrongItemOwnerException e = assertThrows(
                WrongItemOwnerException.class,
                () -> {
                    ItemDto itemDto = new ItemDto(1, "Test item name",
                            "Test item is for doing tests", true, 0);
                    itemStorage.addItem(1, itemDto);
                    ItemDto itemDtoToUpdate = new ItemDto(1, "Updated item name",
                            "Test item is for doing tests", false, 0);
                    itemStorage.updateItem(10, 1, itemDtoToUpdate);
                }
        );

        assertEquals("Пользователь с id=10 не владеет вещью с id=1", e.getMessage());
    }

    @Test
    void shouldDeleteItem() {
        ItemNotFoundException e = assertThrows(
                ItemNotFoundException.class,
                () -> {
                    ItemDto itemDto = new ItemDto(1, "Test item name",
                            "Test item is for doing tests", true, 0);
                    itemStorage.addItem(1, itemDto);
                    itemStorage.deleteItem(1);
                    itemStorage.getItemById(1);
                }
        );

        assertEquals("Вещь с id=1 не найдена", e.getMessage());
    }

    @Test
    void shouldFindItems() {
        ItemDto itemDto = new ItemDto(1, "Test item name",
                "Test item is for doing tests", true, 0);
        itemStorage.addItem(1, itemDto);
        List<ItemDto> items = itemStorage.findItems("DOING");

        assertEquals(1, items.size());
        assertEquals(itemDto, items.get(0));
    }
}
