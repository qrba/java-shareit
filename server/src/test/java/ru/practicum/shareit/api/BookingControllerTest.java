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
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.dto.BookingDtoDefault;
import ru.practicum.shareit.booking.model.dto.BookingDtoOutgoing;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.user.model.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    private final BookingDtoDefault bookingDtoDefault = new BookingDtoDefault(
            1,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2),
            1,
            null,
            null
    );

    private final BookingDtoOutgoing bookingDtoOutgoing = new BookingDtoOutgoing(
            1,
            LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS),
            new ItemDto(
                    1,
                    "item1",
                    "item1 description",
                    true,
                    null,
                    null,
                    null,
                    Collections.emptyList()
            ),
            new UserDto(1, "user1", "user1@email.com"),
            BookingStatus.WAITING
    );

    @Test
    public void shouldAddBooking() throws Exception {
        Mockito
                .when(bookingService.addBooking(any(BookingDtoDefault.class)))
                .thenReturn(bookingDtoOutgoing);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoDefault))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoOutgoing.getId()))
                .andExpect(jsonPath("$.start").value(bookingDtoOutgoing.getStart().toString()))
                .andExpect(jsonPath("$.end").value(bookingDtoOutgoing.getEnd().toString()))
                .andExpect(jsonPath("$.status").value(bookingDtoOutgoing.getStatus().toString()))
                .andExpect(jsonPath("$.item.id").value(bookingDtoOutgoing.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(bookingDtoOutgoing.getBooker().getId()));
    }

    @Test
    public void shouldApproveBooking() throws Exception {
        Mockito
                .when(bookingService.approveBooking(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(bookingDtoOutgoing);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", String.valueOf(true))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoOutgoing.getId()))
                .andExpect(jsonPath("$.start").value(bookingDtoOutgoing.getStart().toString()))
                .andExpect(jsonPath("$.end").value(bookingDtoOutgoing.getEnd().toString()))
                .andExpect(jsonPath("$.status").value(bookingDtoOutgoing.getStatus().toString()))
                .andExpect(jsonPath("$.item.id").value(bookingDtoOutgoing.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(bookingDtoOutgoing.getBooker().getId()));
    }

    @Test
    public void shouldGetBookingById() throws Exception {
        Mockito
                .when(bookingService.getById(anyInt(), anyInt()))
                .thenReturn(bookingDtoOutgoing);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoOutgoing.getId()))
                .andExpect(jsonPath("$.start").value(bookingDtoOutgoing.getStart().toString()))
                .andExpect(jsonPath("$.end").value(bookingDtoOutgoing.getEnd().toString()))
                .andExpect(jsonPath("$.status").value(bookingDtoOutgoing.getStatus().toString()))
                .andExpect(jsonPath("$.item.id").value(bookingDtoOutgoing.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(bookingDtoOutgoing.getBooker().getId()));
    }

    @Test
    public void shouldGetUserBookings() throws Exception {
        Mockito
                .when(bookingService.getUserBookings(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoOutgoing));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(bookingDtoOutgoing.getId()))
                .andExpect(jsonPath("$.[0].start").value(bookingDtoOutgoing.getStart().toString()))
                .andExpect(jsonPath("$.[0].end").value(bookingDtoOutgoing.getEnd().toString()))
                .andExpect(jsonPath("$.[0].status").value(bookingDtoOutgoing.getStatus().toString()))
                .andExpect(jsonPath("$.[0].item.id").value(bookingDtoOutgoing.getItem().getId()))
                .andExpect(jsonPath("$.[0].booker.id").value(bookingDtoOutgoing.getBooker().getId()));
    }

    @Test
    public void shouldGetOwnerBookings() throws Exception {
        Mockito
                .when(bookingService.getOwnerBookings(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoOutgoing));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(bookingDtoOutgoing.getId()))
                .andExpect(jsonPath("$.[0].start").value(bookingDtoOutgoing.getStart().toString()))
                .andExpect(jsonPath("$.[0].end").value(bookingDtoOutgoing.getEnd().toString()))
                .andExpect(jsonPath("$.[0].status").value(bookingDtoOutgoing.getStatus().toString()))
                .andExpect(jsonPath("$.[0].item.id").value(bookingDtoOutgoing.getItem().getId()))
                .andExpect(jsonPath("$.[0].booker.id").value(bookingDtoOutgoing.getBooker().getId()));
    }
}