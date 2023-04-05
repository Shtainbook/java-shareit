package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingListDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDtoResponse> createBooking(@RequestHeader("X-Sharer-User-Id") @Min(1) Long bookerId,
                                                            @Valid @RequestBody BookingDto bookingDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(bookerId, bookingDto));
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<BookingDtoResponse> approveBooking(@RequestHeader("X-Sharer-User-Id") @Min(1) Long ownerId,
                                                             @RequestParam String approved,
                                                             @PathVariable @Min(1) Long bookingId) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.approveBooking(ownerId, bookingId, approved));
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<BookingDtoResponse> getBookingByIdForOwnerAndBooker(
            @PathVariable @Min(1) Long bookingId,
            @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookingService.getBookingByIdForOwnerAndBooker(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<BookingListDto> getAllBookingsForUser(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                                @RequestParam(defaultValue = "ALL") String state) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getAllBookingsForUser(userId, state));
    }

    @GetMapping("owner")
    public ResponseEntity<BookingListDto> getAllBookingsForItemsUser(
            @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId, @RequestParam(defaultValue = "ALL") String state) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getAllBookingsForItemsUser(userId, state));
    }
}
