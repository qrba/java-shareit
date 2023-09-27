package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.model.ItemMapper.dtoToItem;
import static ru.practicum.shareit.item.model.ItemMapper.itemToDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable int itemId) {
        Item item = itemService.getItemById(itemId);
        return itemToDto(item);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getItemsByUserId(userId).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody @Valid ItemDto itemDto) {
        Item item = dtoToItem(userId, itemDto);
        return itemToDto(itemService.addItem(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") int userId,
                           @PathVariable int itemId, @RequestBody ItemDto itemDto) {
        Item item = dtoToItem(userId, itemDto);
        item.setId(itemId);
        return itemToDto(itemService.updateItem(item));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> findItems(@RequestParam String text) {
        return itemService.findItems(text).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }
}