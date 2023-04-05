package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;


public interface ItemService {
    ItemDtoResponse createItem(ItemDto item, Long userId);

    ItemDtoResponse updateItem(Long itemId, Long userId, ItemDtoUpdate item);

    ItemDtoResponse getItemByItemId(Long userId, Long itemId);

    ItemListDto getPersonalItems(Long userId);

    ItemListDto getFoundItems(String text);

    CommentDtoResponse addComment(Long itemId, Long userId, CommentDto commentDto);
}
