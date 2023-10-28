package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static ru.practicum.shareit.request.model.ItemRequestMapper.itemRequestFromDto;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    private ItemRequestStorage itemRequestStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private ItemStorage itemStorage;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    public void setUp() {
        user = new User(1, "user1", "user1@email.com");

        itemRequestDto = new ItemRequestDto(
                1,
                "Test description",
                null,
                Collections.emptyList()
        );
    }

    @Test
    public void shouldAddItemRequest() {
        Mockito
                .when(userStorage.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRequestStorage.save(any(ItemRequest.class)))
                .then(returnsFirstArg());

        ItemRequestDto itemRequestDtoOutgoing = itemRequestService.addItemRequest(1, itemRequestDto);

        assertThat(itemRequestDto.getId(), equalTo(itemRequestDtoOutgoing.getId()));
        assertThat(itemRequestDto.getDescription(), equalTo(itemRequestDtoOutgoing.getDescription()));
        assertThat(itemRequestDto.getItems(), equalTo(itemRequestDtoOutgoing.getItems()));
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

        assertEquals("Пользователь с id=1 не найден", e.getMessage());
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

        assertThat(1, equalTo(itemRequests.size()));
        assertThat(itemRequestDto.getId(), equalTo(itemRequestDtoOutgoing.getId()));
        assertThat(itemRequestDto.getDescription(), equalTo(itemRequestDtoOutgoing.getDescription()));
        assertThat(itemRequestDto.getItems(), equalTo(itemRequestDtoOutgoing.getItems()));
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

        assertEquals("Пользователь с id=1 не найден", e.getMessage());
    }

    @Test
    public void getAllItemRequests() {
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

        List<ItemRequestDto> items = itemRequestService.getAllItemRequests(1, 0, 5);
        ItemRequestDto itemRequestDtoOutgoing = items.get(0);

        assertThat(1, equalTo(items.size()));
        assertThat(itemRequestDto.getId(), equalTo(itemRequestDtoOutgoing.getId()));
        assertThat(itemRequestDto.getDescription(), equalTo(itemRequestDtoOutgoing.getDescription()));
        assertThat(itemRequestDto.getItems(), equalTo(itemRequestDtoOutgoing.getItems()));
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

        assertEquals("Пользователь с id=1 не найден", e.getMessage());
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

        assertThat(itemRequestDto.getId(), equalTo(itemRequestDtoOutgoing.getId()));
        assertThat(itemRequestDto.getDescription(), equalTo(itemRequestDtoOutgoing.getDescription()));
        assertThat(itemRequestDto.getItems(), equalTo(itemRequestDtoOutgoing.getItems()));
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

        assertEquals("Пользователь с id=1 не найден", e.getMessage());
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

        assertEquals("Запрос с id=1 не найден", e.getMessage());
    }
}
