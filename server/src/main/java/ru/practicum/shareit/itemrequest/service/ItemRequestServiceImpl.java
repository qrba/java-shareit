package ru.practicum.shareit.itemrequest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.itemrequest.model.ItemRequest;
import ru.practicum.shareit.itemrequest.model.ItemRequestDto;
import ru.practicum.shareit.itemrequest.model.ItemRequestMapper;
import ru.practicum.shareit.itemrequest.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.itemrequest.model.ItemRequestMapper.itemRequestFromDto;
import static ru.practicum.shareit.itemrequest.model.ItemRequestMapper.itemRequestToDto;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestStorage itemRequestStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    public ItemRequestDto addItemRequest(int userId, ItemRequestDto itemRequestDto) {
        Optional<User> userOptional = userStorage.findById(userId);
        if (userOptional.isEmpty()) throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        ItemRequest itemRequest = itemRequestStorage.save(itemRequestFromDto(itemRequestDto, userOptional.get()));
        log.info("Пользователем с id={} добавлен запрос {}", userId, itemRequest);
        return itemRequestToDto(itemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getItemRequestsByUserId(int userId) {
        if (!userStorage.existsById(userId))
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        log.info("Пользователь с id={} запросил свои запросы", userId);
        return itemRequestStorage.findByRequestorIdOrderByCreatedDesc(userId).stream()
                .peek(itemRequest -> itemRequest.setItems(itemStorage.findByRequestId(itemRequest.getId())))
                .map(ItemRequestMapper::itemRequestToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllItemRequests(int userId, int from, int size) {
        if (!userStorage.existsById(userId))
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        int page = from / size;
        log.info("Пользователь с id={} запросил запросы других пользователей", userId);
        return itemRequestStorage.findByRequestorIdNotOrderByCreatedDesc(userId, PageRequest.of(page, size)).stream()
                .peek(itemRequest -> itemRequest.setItems(itemStorage.findByRequestId(itemRequest.getId())))
                .map(ItemRequestMapper::itemRequestToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getItemRequestById(int userId, int requestId) {
        if (!userStorage.existsById(userId))
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        Optional<ItemRequest> itemRequestOptional = itemRequestStorage.findById(requestId);
        if (itemRequestOptional.isEmpty())
            throw new ItemRequestNotFoundException("Запрос с id=" + requestId + " не найден");
        log.info("Пользователь с id={} запросил запрос с id={}", userId, requestId);
        ItemRequest itemRequest = itemRequestOptional.get();
        itemRequest.setItems(itemStorage.findByRequestId(requestId));
        return itemRequestToDto(itemRequest);
    }
}