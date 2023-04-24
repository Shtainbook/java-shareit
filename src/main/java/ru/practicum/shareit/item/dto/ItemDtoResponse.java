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
    private ItemRequest request;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;

    private Set<CommentDtoResponse> comments;

    @Override
    public String toString() {
        return "ItemDtoResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", available=" + available +
                ", request=" + request +
                ", lastBooking=" + lastBooking +
                ", nextBooking=" + nextBooking +
                ", comments=" + comments +
                '}';
    }
}
