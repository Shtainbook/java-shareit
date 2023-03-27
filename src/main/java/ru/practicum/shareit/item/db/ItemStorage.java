package ru.practicum.shareit.item.db;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item, User user);

    Item readItem(Long id);

    List<Item> readAllItem();

    Item updateItem(Long itemId, User user, Item item);

    Item deleteItem(Long id);

    List<Item> searchPersonalItems(Long id);

    List<Item> searchItemsByName(String text);
}
