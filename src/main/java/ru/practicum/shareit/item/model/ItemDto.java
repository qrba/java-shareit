package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDto {
    private Integer id;
    @NotBlank(message = "Имя вещи не может быть пустым")
    private final String name;
    @NotBlank(message = "Описание вещи не может быть пустым")
    private final String description;
    @JsonProperty("available")
    @NotNull(message = "Должна быть указана доступность вещи")
    private final Boolean isAvailable;
    private final Integer requestId;
}