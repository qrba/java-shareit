package ru.practicum.shareit.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    private final ItemDto itemDto = new ItemDto(
            1,
            "item1",
            "item1 description",
            true,
            null,
            null,
            null,
            Collections.emptyList()
    );

    @Test
    public void shouldGetItemById() throws Exception {
        Mockito
                .when(itemService.getItemById(anyInt(), anyInt()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));
    }

    @Test
    public void shouldGetItemsByUserId() throws Exception {
        Mockito
                .when(itemService.getItemsByUserId(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$.[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$.[0].description").value(itemDto.getDescription()));
    }

    @Test
    public void shouldAddItem() throws Exception {
        Mockito
                .when(itemService.addItem(anyInt(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));
    }

    @Test
    public void shouldNotAddItemWhenBlankName() throws Exception {
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(
                                new ItemDto(
                                        1,
                                        "",
                                        "item1 description",
                                        true,
                                        null,
                                        null,
                                        null,
                                        Collections.emptyList()
                                )
                        ))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Имя вещи не может быть пустым"));
    }

    @Test
    public void shouldNotAddItemWhenBlankDescription() throws Exception {
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(
                                new ItemDto(
                                        1,
                                        "item1",
                                        "",
                                        true,
                                        null,
                                        null,
                                        null,
                                        Collections.emptyList()
                                )
                        ))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Описание вещи не может быть пустым"));
    }

    @Test
    public void shouldNotAddItemWhenIsAvailableNull() throws Exception {
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(
                                new ItemDto(
                                        1,
                                        "item1",
                                        "item1 description",
                                        null,
                                        null,
                                        null,
                                        null,
                                        Collections.emptyList()
                                )
                        ))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Должна быть указана доступность вещи"));
    }

    @Test
    public void shouldUpdateItem() throws Exception {
        Mockito
                .when(itemService.updateItem(anyInt(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));
    }

    @Test
    public void shouldDeleteItem() throws Exception {
        mvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(itemService).deleteItem(anyInt(), anyInt());
    }

    @Test
    public void shouldFindItems() throws Exception {
        Mockito
                .when(itemService.findItems(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "item")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$.[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$.[0].description").value(itemDto.getDescription()));
    }

    @Test
    public void shouldAddComment() throws Exception {
        CommentDto commentDto = new CommentDto(
                1,
                "test comment text",
                "user1",
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
        Mockito
                .when(itemService.addComment(anyInt(), anyInt(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.created").value(commentDto.getCreated().toString()));
    }

    @Test
    public void shouldNotAddCommentWhenBlankText() throws Exception {
        CommentDto commentDto = new CommentDto(
                1,
                "",
                "user1",
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Текст комментария не может быть пустым"));
    }
}
