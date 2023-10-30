package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Integer> {
    List<Booking> findByBookerIdOrderByStartDesc(int id, Pageable pageable);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(int id, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(int id,
                                                                          LocalDateTime forStart,
                                                                          LocalDateTime forEnd,
                                                                          Pageable pageable);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(int id, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(int id, BookingStatus status, Pageable pageable);

    List<Booking> findByItemOwnerIdOrderByStartDesc(int id, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(int id,
                                                                 LocalDateTime localDateTime,
                                                                 Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(int id,
                                                                             LocalDateTime forStart,
                                                                             LocalDateTime forEnd,
                                                                             Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(int id, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(int id, BookingStatus status, Pageable pageable);

    Booking findFirstByItemIdAndStatusNotAndStartAfterOrderByStartAsc(int itemId,
                                                                             BookingStatus status,
                                                                             LocalDateTime localDateTime);

    Booking findFirstByItemIdAndStatusNotAndStartBeforeOrderByStartDesc(int itemId,
                                                                               BookingStatus status,
                                                                               LocalDateTime localDateTime);

    Boolean existsByBookerIdAndItemIdAndEndBefore(int bookerId, int itemId, LocalDateTime localDateTime);

    Optional<Booking> findByIdAndItemOwnerId(int id, int ownerId);
}