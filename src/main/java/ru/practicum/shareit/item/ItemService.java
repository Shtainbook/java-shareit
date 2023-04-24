package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.*;

import java.util.List;


public interface ItemService {
    ItemDtoResponse createItem(ItemDto item, Long userId);

    ItemDtoResponse updateItem(Long itemId, Long userId, ItemDtoUpdate item);

    ItemDtoResponse getItemByItemId(Long userId, Long itemId);

    List<ItemDtoResponse> getPersonalItems(Pageable pageable, Long userId);

    List<ItemDtoResponse> getFoundItems(Pageable pageable, String text);

    CommentDtoResponse addComment(Long itemId, Long userId, CommentDto commentDto);
}
