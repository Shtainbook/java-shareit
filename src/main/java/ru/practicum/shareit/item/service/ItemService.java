package ru.practicum.shareit.item.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemListDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto item, Long userId);

    ItemDto readItem(Long id);

    List<ItemDto> readAllItem();

    ItemDto updateItem(Long itemId, Long userId, ItemDto item);

    ItemDto deleteItem(Long id);

    List<ItemDto> searchPersonalItems(Long id);

    ItemListDto searchItemsByName(String text);
}
