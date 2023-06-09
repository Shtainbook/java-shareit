package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Getter
@Setter
public class RequestDtoResponseWithMD {
    private Long id;
    private String description;
    private LocalDateTime created;
    private Set<ItemDataForRequestDto> items;
}
