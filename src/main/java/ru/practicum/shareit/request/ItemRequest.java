package ru.practicum.shareit.request;

import lombok.Data;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    private Integer id;
    private final String description;
    private final Integer requestor;
    private final Long created;
}
