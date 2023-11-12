package ru.practicum.shareit.unit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.item.ItemClient;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemClientTest {
    private final ItemClient itemClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
/*
    @Test
    public void shouldGetItemById() {
        Mockito
                .when(itemStorage.findById(anyInt()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(bookingStorage.findFirstByItemIdAndStatusNotAndStartBeforeOrderByStartDesc(
                        anyInt(),
                        any(BookingStatus.class),
                        any(LocalDateTime.class)
                ))
                .thenReturn(null);
        Mockito
                .when(bookingStorage.findFirstByItemIdAndStatusNotAndStartAfterOrderByStartAsc(
                        anyInt(),
                        any(BookingStatus.class),
                        any(LocalDateTime.class)
                ))
                .thenReturn(booking);
        Mockito
                .when(commentStorage.findByItemIdOrderByCreatedDesc(anyInt()))
                .thenReturn(List.of(comment));

        ItemDto itemDtoOutgoing = itemService.getItemById(1, 1);

        assertThat(itemDtoOutgoing.getId(), equalTo(item.getId()));
        assertThat(itemDtoOutgoing.getName(), equalTo(item.getName()));
        assertThat(itemDtoOutgoing.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDtoOutgoing.getIsAvailable(), equalTo(item.getIsAvailable()));
        assertThat(itemDtoOutgoing.getRequestId(), equalTo(item.getRequest().getId()));
        assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
        assertThat(itemDtoOutgoing.getNextBooking().getId(), equalTo(booking.getId()));
        assertThat(itemDtoOutgoing.getComments().size(), equalTo(1));
        assertThat(itemDtoOutgoing.getComments().get(0).getId(), equalTo(comment.getId()));
    }

    @Test
    public void shouldNotGetItemByIdWhenItemNotFound() {
        Mockito
                .when(itemStorage.findById(anyInt()))
                .thenReturn(Optional.empty());

        ItemNotFoundException e = Assertions.assertThrows(
                ItemNotFoundException.class,
                () -> itemService.getItemById(1, 1)
        );

        assertThat(e.getMessage(), equalTo("Вещь с id=1 не найдена"));
    }

    @Test
    public void shouldGetItemsByUserId() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(itemStorage.findByOwnerIdOrderById(anyInt(), any(Pageable.class)))
                .thenReturn(List.of(item));
        Mockito
                .when(bookingStorage.findFirstByItemIdAndStatusNotAndStartBeforeOrderByStartDesc(
                        anyInt(),
                        any(BookingStatus.class),
                        any(LocalDateTime.class)
                ))
                .thenReturn(null);
        Mockito
                .when(bookingStorage.findFirstByItemIdAndStatusNotAndStartAfterOrderByStartAsc(
                        anyInt(),
                        any(BookingStatus.class),
                        any(LocalDateTime.class)
                ))
                .thenReturn(booking);
        Mockito
                .when(commentStorage.findByItemIdOrderByCreatedDesc(anyInt()))
                .thenReturn(List.of(comment));

        List<ItemDto> items = itemService.getItemsByUserId(1, 0, 5);
        ItemDto itemDtoOutgoing = items.get(0);

        assertThat(items.size(), equalTo(1));
        assertThat(itemDtoOutgoing.getId(), equalTo(item.getId()));
        assertThat(itemDtoOutgoing.getName(), equalTo(item.getName()));
        assertThat(itemDtoOutgoing.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDtoOutgoing.getIsAvailable(), equalTo(item.getIsAvailable()));
        assertThat(itemDtoOutgoing.getRequestId(), equalTo(item.getRequest().getId()));
        assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
        assertThat(itemDtoOutgoing.getNextBooking().getId(), equalTo(booking.getId()));
        assertThat(itemDtoOutgoing.getComments().size(), equalTo(1));
        assertThat(itemDtoOutgoing.getComments().get(0).getId(), equalTo(comment.getId()));
    }

    @Test
    public void shouldNotGetItemsByUserIdWhenUserNotFound() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(false);

        UserNotFoundException e = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> itemService.getItemsByUserId(1, 0, 5)
        );

        assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
    }

    @Test
    public void shouldAddItem() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRequestStorage.findById(anyInt()))
                .thenReturn(Optional.of(itemRequest));
        Mockito
                .when(itemStorage.save(any(Item.class)))
                .then(returnsFirstArg());

        ItemDto itemDtoOutgoing = itemService.addItem(1, itemToDto(item, null, null, null));

        assertThat(itemDtoOutgoing.getId(), equalTo(item.getId()));
        assertThat(itemDtoOutgoing.getName(), equalTo(item.getName()));
        assertThat(itemDtoOutgoing.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDtoOutgoing.getIsAvailable(), equalTo(item.getIsAvailable()));
        assertThat(itemDtoOutgoing.getRequestId(), equalTo(item.getRequest().getId()));
        assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
        assertThat(itemDtoOutgoing.getNextBooking(), nullValue());
        assertThat(itemDtoOutgoing.getComments(), nullValue());
    }

    @Test
    public void shouldNotAddItemWhenUserNotFound() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.empty());

        UserNotFoundException e = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> itemService.addItem(1, itemToDto(item, null, null, null))
        );

        assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
    }

    @Test
    public void shouldNotAddItemWhenItemRequestNotFound() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRequestStorage.findById(anyInt()))
                .thenReturn(Optional.empty());

        ItemRequestNotFoundException e = Assertions.assertThrows(
                ItemRequestNotFoundException.class,
                () -> itemService.addItem(1, itemToDto(item, null, null, null))
        );

        assertThat(e.getMessage(), equalTo("Запрос с id=1 не найден"));
    }

    @Test
    public void shouldUpdateItem() {
        Mockito
                .when(itemStorage.findById(anyInt()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(itemRequestStorage.findById(anyInt()))
                .thenReturn(Optional.of(itemRequest));
        Mockito
                .when(itemStorage.save(any(Item.class)))
                .then(returnsFirstArg());

        ItemDto itemDtoOutgoing = itemService.updateItem(1, itemToDto(item, null, null, null));

        assertThat(itemDtoOutgoing.getId(), equalTo(item.getId()));
        assertThat(itemDtoOutgoing.getName(), equalTo(item.getName()));
        assertThat(itemDtoOutgoing.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDtoOutgoing.getIsAvailable(), equalTo(item.getIsAvailable()));
        assertThat(itemDtoOutgoing.getRequestId(), equalTo(item.getRequest().getId()));
        assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
        assertThat(itemDtoOutgoing.getNextBooking(), nullValue());
        assertThat(itemDtoOutgoing.getComments(), nullValue());
    }

    @Test
    public void shouldNotUpdateItemWhenItemNotFound() {
        Mockito
                .when(itemStorage.findById(anyInt()))
                .thenReturn(Optional.empty());

        ItemNotFoundException e = Assertions.assertThrows(
                ItemNotFoundException.class,
                () -> itemService.updateItem(1, itemToDto(item, null, null, null))
        );

        assertThat(e.getMessage(), equalTo("Вещь с id=1 не найдена"));
    }

    @Test
    public void shouldNotUpdateItemWhenUserNotOwner() {
        Mockito
                .when(itemStorage.findById(anyInt()))
                .thenReturn(Optional.of(item));
        ItemOwnerException e = Assertions.assertThrows(
                ItemOwnerException.class,
                () -> itemService.updateItem(2, itemToDto(item, null, null, null))
        );

        assertThat(e.getMessage(), equalTo("Пользователь с id=2 не владеет вещью с id=1"));
    }

    @Test
    public void shouldNotUpdateItemWhenItemRequestNotFound() {
        Mockito
                .when(itemStorage.findById(anyInt()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(itemRequestStorage.findById(anyInt()))
                .thenReturn(Optional.empty());

        ItemRequestNotFoundException e = Assertions.assertThrows(
                ItemRequestNotFoundException.class,
                () -> itemService.updateItem(1, itemToDto(item, null, null, null))
        );

        assertThat(e.getMessage(), equalTo("Запрос с id=1 не найден"));
    }

    @Test
    public void shouldDeleteItem() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(true);

        itemService.deleteItem(1, 1);

        Mockito.verify(itemStorage).deleteById(anyInt());
    }

    @Test
    public void shouldNotDeleteItemWhenUserNotFound() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(false);

        UserNotFoundException e = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> itemService.deleteItem(1, 1)
        );

        assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
    }

    @Test
    public void shouldFindItems() {
        Mockito
                .when(itemStorage.findByText(any(String.class), any(Pageable.class)))
                .thenReturn(List.of(item));

        List<ItemDto> items = itemService.findItems("Test", 0, 5);
        ItemDto itemDtoOutgoing = items.get(0);

        assertThat(itemDtoOutgoing.getId(), equalTo(item.getId()));
        assertThat(itemDtoOutgoing.getName(), equalTo(item.getName()));
        assertThat(itemDtoOutgoing.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDtoOutgoing.getIsAvailable(), equalTo(item.getIsAvailable()));
        assertThat(itemDtoOutgoing.getRequestId(), equalTo(item.getRequest().getId()));
        assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
        assertThat(itemDtoOutgoing.getNextBooking(), nullValue());
        assertThat(itemDtoOutgoing.getComments(), nullValue());
    }

    @Test
    public void shouldNotFindItemsWhenBlankText() {
        List<ItemDto> items = itemService.findItems("", 0, 5);

        assertThat(0, equalTo(items.size()));
    }

    @Test
    public void shouldAddComment() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemStorage.findById(anyInt()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(
                        bookingStorage.existsByBookerIdAndItemIdAndEndBefore(
                                anyInt(),
                                anyInt(),
                                any(LocalDateTime.class)
                        )
                )
                .thenReturn(true);
        Mockito
                .when(commentStorage.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto commentDtoOutgoing = itemService.addComment(
                1,
                1,
                commentToDto(comment, user.getName())
        );

        assertThat(commentDtoOutgoing.getId(), equalTo(comment.getId()));
        assertThat(commentDtoOutgoing.getText(), equalTo(comment.getText()));
        assertThat(commentDtoOutgoing.getAuthorName(), equalTo(comment.getAuthor().getName()));
        assertThat(commentDtoOutgoing.getCreated(), equalTo(comment.getCreated()));
    }

    @Test
    public void shouldNotAddCommentWhenUserNotFound() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.empty());

        UserNotFoundException e = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> itemService.addComment(1, 1, commentToDto(comment, user.getName()))
        );

        assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
    }

    @Test
    public void shouldNotAddCommentWhenItemNotFound() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemStorage.findById(anyInt()))
                .thenReturn(Optional.empty());

        ItemNotFoundException e = Assertions.assertThrows(
                ItemNotFoundException.class,
                () -> itemService.addComment(1, 1, commentToDto(comment, user.getName()))
        );

        assertThat(e.getMessage(), equalTo("Вещь с id=1 не найдена"));
    }

    @Test
    public void shouldNotAddCommentWhenBookingNotEnded() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemStorage.findById(anyInt()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(
                        bookingStorage.existsByBookerIdAndItemIdAndEndBefore(
                                anyInt(),
                                anyInt(),
                                any(LocalDateTime.class)
                        )
                )
                .thenReturn(false);

        BookingEndTimeException e = Assertions.assertThrows(
                BookingEndTimeException.class,
                () -> itemService.addComment(1, 1, commentToDto(comment, user.getName()))
        );

        assertThat(e.getMessage(), equalTo("Бронирование еще не завершилось"));
    }*/
}