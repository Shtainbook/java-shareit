package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.ToString;

import java.util.List;

@Builder
@ToString
public class BookingListDto {
    @JsonValue
    private List<BookingDtoResponse> bookings;
}
