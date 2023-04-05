package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.request.ItemRequest;

import java.util.Set;

@Builder
@Getter
@Setter
public class ItemDtoResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    //private User owner;
    private ItemRequest request;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private Set<CommentDtoResponse> comments;
}
