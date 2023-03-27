package ru.practicum.shareit.item;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class ItemMapper {

    public static ItemDto toItemDTO(Item item) {
        if (item == null) {
            return null;
        }
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest()
        );
    }

    public static Item dtoToItem(ItemDto itemDto) {
        if (itemDto == null) {
            return null;
        }
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                itemDto.getRequest()
        );
    }

    public static List<ItemDto> toListItemDto(List<Item> items) {
        List<ItemDto> itemDto = new ArrayList<>();
        if (items == null) {
            return itemDto;
        }
        return items.stream().map(ItemMapper::toItemDTO).collect(Collectors.toList());
    }
}