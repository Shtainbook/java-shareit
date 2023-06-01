package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;


public interface BookingService {

    BookingDtoResponse createBooking(Long bookerId, BookingDto bookingDto);

    BookingDtoResponse approveBooking(Long ownerId, Long bookingId, String approved);

    BookingDtoResponse getBookingByIdForOwnerAndBooker(Long bookingId, Long userId);

    List<BookingDtoResponse> getAllBookingsForUser(Pageable pageable, Long userId, String state);

    List<BookingDtoResponse> getAllBookingsForItemsUser(Pageable pageable, Long userId, String state);
}
