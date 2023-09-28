package ru.practicum.shareit.item.model;

public class ItemMapper {
    public static ItemDto itemToDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getRequestId()
        );
    }

    public static Item dtoToItem(int userId, ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(userId)
                .isAvailable(itemDto.getIsAvailable())
                .requestId(itemDto.getRequestId())
                .build();
    }
}
