package ru.practicum.shareit.itemrequest;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.itemrequest.model.ItemRequestDto;
import ru.practicum.shareit.itemrequest.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto addItemRequest(
            @RequestHeader(USER_ID_HEADER) int userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto
    ) {
        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestsByUserId(@RequestHeader(USER_ID_HEADER) int userId) {
        return itemRequestService.getItemRequestsByUserId(userId);
    }

    @GetMapping(path = "/all")
    public List<ItemRequestDto> getAllItemRequests(
            @RequestHeader(USER_ID_HEADER) int userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(
            @RequestHeader(USER_ID_HEADER) int userId,
            @PathVariable int requestId
    ) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}