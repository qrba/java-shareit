package ru.practicum.shareit.unit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.itemrequest.ItemRequestClient;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestClientTest {
    private final ItemRequestClient itemRequestClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
/*
    @Test
    public void shouldAddItemRequest() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRequestStorage.save(any(ItemRequest.class)))
                .then(returnsFirstArg());

        ItemRequestDto itemRequestDtoOutgoing = itemRequestService.addItemRequest(1, itemRequestDto);

        assertThat(itemRequestDtoOutgoing.getId(), equalTo(itemRequestDto.getId()));
        assertThat(itemRequestDtoOutgoing.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequestDtoOutgoing.getItems(), equalTo(itemRequestDto.getItems()));
    }

    @Test
    public void shouldNotAddItemRequestWhenUserNotFound() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.empty());

        UserNotFoundException e = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.addItemRequest(1, itemRequestDto)
        );

        assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
    }

    @Test
    public void shouldGetItemRequestsByUserId() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(itemRequestStorage.findByRequestorIdOrderByCreatedDesc(anyInt()))
                .thenReturn(
                        List.of(itemRequestFromDto(itemRequestDto, user))
                );
        Mockito
                .when(itemStorage.findByRequestId(anyInt()))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> itemRequests = itemRequestService.getItemRequestsByUserId(1);
        ItemRequestDto itemRequestDtoOutgoing = itemRequests.get(0);

        assertThat(itemRequests.size(), equalTo(1));
        assertThat(itemRequestDtoOutgoing.getId(), equalTo(itemRequestDto.getId()));
        assertThat(itemRequestDtoOutgoing.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequestDtoOutgoing.getItems(), equalTo(itemRequestDto.getItems()));
    }

    @Test
    public void shouldNotGetItemRequestsByUserIdWhenUserNotFound() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(false);

        UserNotFoundException e = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.getItemRequestsByUserId(1)
        );

        assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
    }

    @Test
    public void shouldGetAllItemRequests() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(itemRequestStorage.findByRequestorIdNotOrderByCreatedDesc(anyInt(), any(Pageable.class)))
                .thenReturn(
                        List.of(itemRequestFromDto(itemRequestDto, user))
                );
        Mockito
                .when(itemStorage.findByRequestId(anyInt()))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> itemRequests = itemRequestService.getAllItemRequests(1, 0, 5);
        ItemRequestDto itemRequestDtoOutgoing = itemRequests.get(0);

        assertThat(itemRequests.size(), equalTo(1));
        assertThat(itemRequestDtoOutgoing.getId(), equalTo(itemRequestDto.getId()));
        assertThat(itemRequestDtoOutgoing.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequestDtoOutgoing.getItems(), equalTo(itemRequestDto.getItems()));
    }

    @Test
    public void shouldNotGetAllItemRequestsWhenUserNotFound() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(false);

        UserNotFoundException e = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.getAllItemRequests(1, 0, 5)
        );

        assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
    }

    @Test
    public void shouldGetItemRequestById() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(itemRequestStorage.findById(anyInt()))
                .thenReturn(
                        Optional.of(itemRequestFromDto(itemRequestDto, user))
                );
        Mockito
                .when(itemStorage.findByRequestId(anyInt()))
                .thenReturn(Collections.emptyList());

        ItemRequestDto itemRequestDtoOutgoing = itemRequestService.getItemRequestById(1,1);

        assertThat(itemRequestDtoOutgoing.getId(), equalTo(itemRequestDto.getId()));
        assertThat(itemRequestDtoOutgoing.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequestDtoOutgoing.getItems(), equalTo(itemRequestDto.getItems()));
    }

    @Test
    public void shouldNotGetItemRequestByIdWhenUserNotFound() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(false);

        UserNotFoundException e = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.getItemRequestById(1, 1)
        );

        assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
    }

    @Test
    public void shouldNotGetItemRequestByIdWhenItemRequestNotFound() {
        Mockito
                .when(userStorage.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(itemRequestStorage.findById(anyInt()))
                .thenReturn(Optional.empty());

        ItemRequestNotFoundException e = Assertions.assertThrows(
                ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequestById(1,1)
        );

        assertThat(e.getMessage(), equalTo("Запрос с id=1 не найден"));
    }*/
}