package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    //		log.info("Get booking {}, userId={}", bookingId, userId);
    @GetMapping("{bookingId}")
    public ResponseEntity<Object> getBookingByIdForOwnerAndBooker(
            @PathVariable @Min(1) Long bookingId,
            @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        log.info("В результате вызова метода getBookingByIdForOwnerAndBooker для userId={}", userId);
        return bookingClient.getBookingByIdForOwnerAndBooker(bookingId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") @Min(1) Long bookerId,
                                                @RequestBody @Valid BookingDto bookingDto) {
        log.info("Создание бронирования пользователем bookerId={}", bookerId);


        return bookingClient.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") @Min(1) Long ownerId,
                                                 @RequestParam String approved,
                                                 @PathVariable @Min(1) Long bookingId) {
        log.info("Букинг подтвержден ownerID:={} и bookingId:={}", ownerId, bookingId);
        return bookingClient.approveBooking(ownerId, approved, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsForUser(
            @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam (value = "from", defaultValue = "0") @Min(0)Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20)Integer size) {

//        if (state == null) state = "ALL";
//        String finalStateParam = state;
//        BookingState param = null;
//
//        try {
//            param = BookingState.from(finalStateParam).get();
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Unknown state: " + finalStateParam);
//        }
//
//        //orElseThrow(() ->



        log.info("В результате вызова метода getAllBookingsForUser для userId:={}", userId);
        return bookingClient.getAllBookingsForUser(userId, state, from, size);
    }

    @GetMapping("owner")
    public ResponseEntity<Object> getAllBookingsForItemsUser(
            @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20)Integer size) {

//        if (state == null) state = "ALL";
//        String finalStateParam = state;
//        BookingState param = null;
//
//        try {
//            param = BookingState.from(finalStateParam).get();
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Unknown state: " + finalStateParam);
//        }

        log.info("В результате вызова метода getAllBookingsForItemsUser для userId:={}", userId);
        return bookingClient.getAllBookingsForItemsUser(userId, state, from, size);
    }
}