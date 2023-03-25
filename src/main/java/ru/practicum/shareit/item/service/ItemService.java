package ru.practicum.shareit.item.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemListDto;

import java.util.List;

public interface ItemService {
    ResponseEntity<ItemDto> createItem(ItemDto item, Long userId);

    ResponseEntity<ItemDto> readItem(Long id);

    ResponseEntity<List<ItemDto>> readAllItem();

    ResponseEntity<ItemDto> updateItem(Long itemId, Long userId, ItemDto item);

    ResponseEntity<ItemDto> deleteItem(Long id);

    ResponseEntity<List<ItemDto>> searchPersonalItems(Long id);

    ResponseEntity<ItemListDto> searchItemsByName(String text);
}
