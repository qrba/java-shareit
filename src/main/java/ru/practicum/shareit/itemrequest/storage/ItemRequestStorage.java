package ru.practicum.shareit.itemrequest.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.itemrequest.model.ItemRequest;

import java.util.List;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Integer> {
    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(int userId);

    List<ItemRequest> findByRequestorIdNotOrderByCreatedDesc(int userId, Pageable pageable);
}
