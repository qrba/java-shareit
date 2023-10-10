package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.model.BookingDtoDefault;
import ru.practicum.shareit.booking.model.BookingDtoOutgoing;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOutgoing addBooking(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @Valid @RequestBody BookingDtoDefault bookingDtoDefault
    ) {
        bookingDtoDefault.setBookerId(userId);
        bookingDtoDefault.setStatus(BookingStatus.WAITING);
        return bookingService.addBooking(bookingDtoDefault);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOutgoing approveBooking(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable int bookingId,
            @RequestParam boolean approved
    ) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOutgoing getById(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoOutgoing> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoOutgoing> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingService.getOwnerBookings(userId, state);
    }
}
