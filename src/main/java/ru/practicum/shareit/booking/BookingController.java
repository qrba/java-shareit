package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.model.dto.BookingDtoDefault;
import ru.practicum.shareit.booking.model.dto.BookingDtoOutgoing;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDtoOutgoing addBooking(
            @RequestHeader(USER_ID_HEADER) int userId,
            @Valid @RequestBody BookingDtoDefault bookingDtoDefault
    ) {
        bookingDtoDefault.setBookerId(userId);
        bookingDtoDefault.setStatus(BookingStatus.WAITING);
        return bookingService.addBooking(bookingDtoDefault);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOutgoing approveBooking(
            @RequestHeader(USER_ID_HEADER) int userId,
            @PathVariable int bookingId,
            @RequestParam boolean approved
    ) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOutgoing getById(@RequestHeader(USER_ID_HEADER) int userId, @PathVariable int bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoOutgoing> getUserBookings(
            @RequestHeader(USER_ID_HEADER) int userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoOutgoing> getOwnerBookings(
            @RequestHeader(USER_ID_HEADER) int userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return bookingService.getOwnerBookings(userId, state, from, size);
    }
}
