package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@Jacksonized
public class ItemRequestDto {
    @NotBlank(message = "поле text не должно быть пустым")
    @Size(max = 500, message = "Превышена максимальная длина сообщения")
    private String description;
//    @FutureOrPresent
//    private LocalDateTime created;//++
//    private List<ItemDto> items;//++
}