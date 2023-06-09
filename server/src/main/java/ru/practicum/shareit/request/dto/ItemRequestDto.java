package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * TODO Sprint add-item-requests.
 */
@Builder
@Data
@Jacksonized
public class ItemRequestDto {
    private String description;
}