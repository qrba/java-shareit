package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.BookingEndTimeException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemOwnerException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.model.CommentMapper.commentFromDto;
import static ru.practicum.shareit.item.model.CommentMapper.commentToDto;
import static ru.practicum.shareit.item.model.ItemMapper.itemFromDto;
import static ru.practicum.shareit.item.model.ItemMapper.itemToDto;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(int userId, int itemId) {
        Optional<Item> itemOptional = itemStorage.findById(itemId);
        if (itemOptional.isEmpty()) throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
        Item item = itemOptional.get();
        Booking last = null;
        Booking next = null;
        if (item.getOwner().getId() == userId) {
            LocalDateTime now = LocalDateTime.now();
            last = bookingStorage.findFirstByItemIdAndStatusNotAndStartBeforeOrderByStartDesc(
                    itemId,
                    BookingStatus.REJECTED,
                    now
            );
            next = bookingStorage.findFirstByItemIdAndStatusNotAndStartAfterOrderByStartAsc(
                    itemId,
                    BookingStatus.REJECTED,
                    now
            );
        }
        List<CommentDto> comments = commentStorage.findByItemIdOrderByCreatedDesc(itemId).stream()
                .map(comment -> commentToDto(comment, comment.getAuthor().getName()))
                .collect(Collectors.toList());
        log.info("Запрошена вещь {}", item);
        return itemToDto(item, last, next, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByUserId(int userId) {
        if (!userStorage.existsById(userId))
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        log.info("Запрошен список вещей пользователя с id={}", userId);
        return itemStorage.findByOwnerId(userId).stream()
                .map(item -> {
                    LocalDateTime now = LocalDateTime.now();
                    return itemToDto(
                            item,
                            bookingStorage.findFirstByItemIdAndStatusNotAndStartBeforeOrderByStartDesc(
                                    item.getId(),
                                    BookingStatus.REJECTED,
                                    now
                            ),
                            bookingStorage.findFirstByItemIdAndStatusNotAndStartAfterOrderByStartAsc(
                                    item.getId(),
                                    BookingStatus.REJECTED,
                                    now
                            ),
                            commentStorage.findByItemIdOrderByCreatedDesc(item.getId()).stream()
                                    .map(comment -> commentToDto(comment, comment.getAuthor().getName()))
                                    .collect(Collectors.toList())
                    );
                }
                )
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto addItem(int userId, ItemDto itemDto) {
        Optional<User> userOptional = userStorage.findById(userId);
        if (userOptional.isEmpty()) throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        Item item = itemStorage.save(
                itemFromDto(itemDto, userOptional.get())
        );
        log.info("Добавлена вещь {}", item);
        return itemToDto(item, null, null, null);
    }

    @Override
    public ItemDto updateItem(int userId, ItemDto itemDto) {
        int itemId = itemDto.getId();
        Optional<Item> itemOptional = itemStorage.findById(itemId);
        if (itemOptional.isEmpty()) throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
        Item oldItem = itemOptional.get();
        User owner = oldItem.getOwner();
        if (owner.getId() != userId)
            throw new ItemOwnerException("Пользователь с id=" + userId + " не владеет вещью с id=" + itemId);
        Item updateItem = itemFromDto(itemDto, owner);

        String name = updateItem.getName();
        String description = updateItem.getDescription();
        if (name == null || name.isBlank()) updateItem.setName(oldItem.getName());
        if (description == null || description.isBlank()) updateItem.setDescription(oldItem.getDescription());
        if (updateItem.getIsAvailable() == null) updateItem.setIsAvailable(oldItem.getIsAvailable());

        Item item = itemStorage.save(updateItem);
        log.info("Обновлена вещь {}", item);
        return itemToDto(item, null, null, null);
    }

    @Override
    public void deleteItem(int userId, int itemId) {
        if (!userStorage.existsById(userId))
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        itemStorage.deleteById(itemId);
        log.info("Удалена вещь c id={}", itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> findItems(String text) {
        if (text.isBlank()) return Collections.emptyList();
        log.info("Запрошен поиск по тексту '{}'", text);
        return itemStorage.findByText(text).stream()
                .map(item -> itemToDto(item, null, null, null))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(int userId, int itemId, CommentDto commentDto) {
        Optional<User> authorOptional = userStorage.findById(userId);
        if (authorOptional.isEmpty()) throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        User author = authorOptional.get();
        Optional<Item> itemOptional = itemStorage.findById(itemId);
        if (itemOptional.isEmpty()) throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
        Item item = itemOptional.get();
        if (!bookingStorage.existsByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now()))
            throw new BookingEndTimeException("Бронирование еще не завершилось");
        Comment comment = commentStorage.save(commentFromDto(commentDto, item, author));
        log.info("Добавлен комментарий '{}'", comment);
        return commentToDto(comment, author.getName());
    }
}