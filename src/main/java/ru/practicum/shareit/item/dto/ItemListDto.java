package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ItemListDto {
    @JsonValue
    private List<ItemDto> items;
}
