package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Integer> {
    List<Booking> findByBookerIdOrderByStartDesc(int id);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(int id, LocalDateTime localDateTime);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(int id,
                                                                                 LocalDateTime forStart,
                                                                                 LocalDateTime forEnd);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(int id, LocalDateTime localDateTime);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(int id, BookingStatus status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(int id);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(int id, LocalDateTime localDateTime);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(int id,
                                                                                 LocalDateTime forStart,
                                                                                 LocalDateTime forEnd);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(int id, LocalDateTime localDateTime);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(int id, BookingStatus status);

    Booking findFirstByItemIdAndStatusNotAndStartAfterOrderByStartAsc(int itemId,
                                                                             BookingStatus status,
                                                                             LocalDateTime localDateTime);

    Booking findFirstByItemIdAndStatusNotAndStartBeforeOrderByStartDesc(int itemId,
                                                                               BookingStatus status,
                                                                               LocalDateTime localDateTime);

    Boolean existsByBookerIdAndItemIdAndEndBefore(int bookerId, int itemId, LocalDateTime localDateTime);

    Optional<Booking> findByIdAndItemOwnerId(int id, int ownerId);
}