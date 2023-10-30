package ru.practicum.shareit.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemStorageTest {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Test
    public void shouldFindByText() {
        User user = new User(null, "user", "user@email.com");
        userStorage.save(user);
        Item item = new Item(
                null,
                "item name",
                "item description",
                true,
                user,
                null
        );
        itemStorage.save(item);
        List<Item> items = itemStorage.findByText("item", PageRequest.of(0, 5));

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0), equalTo(item));

        items = itemStorage.findByText("scissors", PageRequest.of(0, 5));

        assertThat(items.size(), equalTo(0));
    }
}