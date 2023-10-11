package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDto commentToDto(Comment comment, String name) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                name,
                comment.getCreated()
        );
    }

    public static Comment commentFromDto(CommentDto commentDto, Item item, User author) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                item,
                author,
                LocalDateTime.now()
        );
    }
}
