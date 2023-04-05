package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;

import java.util.List;

@Builder
public class ItemListDto {
    @JsonValue
    private List<ItemDtoResponse> items;
}
