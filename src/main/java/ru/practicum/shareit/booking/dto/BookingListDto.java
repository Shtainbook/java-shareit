package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;

import java.util.List;

@Builder
public class BookingListDto {
    @JsonValue
    private List<BookingDtoResponse> bookings;
}
