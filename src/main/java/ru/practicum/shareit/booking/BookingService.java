package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingListDto;


public interface BookingService {

    BookingDtoResponse createBooking(Long bookerId, BookingDto bookingDto);

    BookingDtoResponse approveBooking(Long ownerId, Long bookingId, String approved);

    BookingDtoResponse getBookingByIdForOwnerAndBooker(Long bookingId, Long userId);

    BookingListDto getAllBookingsForUser(Long userId, String state);

    BookingListDto getAllBookingsForItemsUser(Long userId, String state);
}
