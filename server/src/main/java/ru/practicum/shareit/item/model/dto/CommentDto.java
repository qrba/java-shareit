package ru.practicum.shareit.item.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private final Integer id;
    private final String text;
    private final String authorName;
    private final LocalDateTime created;
}
