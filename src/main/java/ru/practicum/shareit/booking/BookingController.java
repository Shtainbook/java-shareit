package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping("/bookings")
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDtoResponse> createBooking(@RequestHeader("X-Sharer-User-Id") @Min(1) Long bookerId,
                                                            @Valid @RequestBody BookingDto bookingDto) {
        log.warn("Букинг создан ID: " + bookerId + " и BookingDTO: " + bookingDto + " .");
        return new ResponseEntity<>(bookingService.createBooking(bookerId, bookingDto), HttpStatus.CREATED);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<BookingDtoResponse> approveBooking(@RequestHeader("X-Sharer-User-Id") @Min(1) Long ownerId,
                                                             @RequestParam String approved,
                                                             @PathVariable @Min(1) Long bookingId) {
        log.warn("Букинг подтвержден ownerID: " + ownerId + " и bookingId: " + bookingId + " .");
        return new ResponseEntity<>(bookingService.approveBooking(ownerId, bookingId, approved), HttpStatus.OK);
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<BookingDtoResponse> getBookingByIdForOwnerAndBooker(
            @PathVariable @Min(1) Long bookingId,
            @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        ResponseEntity<BookingDtoResponse> result = ResponseEntity.status(HttpStatus.OK)
                .body(bookingService.getBookingByIdForOwnerAndBooker(bookingId, userId));
        log.warn("В результате вызова метода getBookingByIdForOwnerAndBooker для userId: " + userId + " результат: " + result.getBody() + " .");
        return result;
    }

    @GetMapping
    public ResponseEntity<List<BookingDtoResponse>> getAllBookingsForUser(
            @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        ResponseEntity<List<BookingDtoResponse>> result = ResponseEntity.status(HttpStatus.OK).
                body(bookingService.getAllBookingsForUser(PageRequest.of(from / size, size), userId, state));
        log.warn("В результате вызова метода getAllBookingsForUser для userId: " + userId + " результат: " + result.getBody() + " .");
        return result;
    }

    @GetMapping("owner")
    public ResponseEntity<List<BookingDtoResponse>> getAllBookingsForItemsUser(
            @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        ResponseEntity<List<BookingDtoResponse>> result = ResponseEntity.status(HttpStatus.OK).
                body(bookingService.getAllBookingsForItemsUser(PageRequest.of(from / size, size), userId, state));
        log.warn("В результате вызова метода getAllBookingsForItemsUser для userId: " + userId + " State: " + state + " результат: " + result.getBody() + " .");
        return result;
    }
}