package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingListDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.handler.exception.StateException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookings;
    private final UserRepository users;
    private final ItemRepository items;
    private final BookingMapper mapper;

    @Override
    @Transactional
    public BookingDtoResponse createBooking(Long bookerId, BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Дата окончания бронирования не может быть раньше даты начала");
        }
        Item item = items.findById(bookingDto.getItemId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Предмета с id=" + bookingDto.getItemId() + " нет"));
        if (!item.getOwner().getId().equals(bookerId)) {
            if (item.getAvailable()) {
                User user = users.findById(bookerId).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Пользователя с id=" + bookerId + " нет"));
               Booking booking = mapper.mapToBookingFromBookingDto(bookingDto);
               booking.setItem(item);
               booking.setBooker(user);
                return mapper.mapToBookingDtoResponse(bookings.save(booking));
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Вещь с id=" + item.getId()
                        + " недоступна для бронирования");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Владелец не может забронировать свою вещь");
        }
    }

    @Override
    @Transactional
    public BookingDtoResponse approveBooking(Long ownerId, Long bookingId, String approved) {
        String approve = approved.toLowerCase();
        if (!(approve.equals("true") || approve.equals("false"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неккоректный параметр строки approved");
        }
        Booking booking = bookings.findById(bookingId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронирования с id=" + bookingId + " нет"));
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Невозможно изменить статут брони со статусом " + booking.getStatus());
        }
        if (booking.getItem().getOwner().getId().equals(ownerId)) {
            if (approve.equals("true")) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
            return mapper.mapToBookingDtoResponse(bookings.save(booking));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Пользователь с id=" + ownerId + " не является владельцем вещи с id=" + booking.getItem().getOwner().getId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoResponse getBookingByIdForOwnerAndBooker(Long bookingId, Long userId) {
        Booking booking = bookings.findById(bookingId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронирования с id=" + bookingId + " нет"));
        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id=" + userId
                    + " не является автором бронирования или владельцем вещи, к которой относится бронирование");
        }
        return mapper.mapToBookingDtoResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingListDto getAllBookingsForUser(Long userId, String state) {
        if (!users.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + userId + " не существует");
        } else {
            return getListBookings(state, userId, false);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookingListDto getAllBookingsForItemsUser(Long userId, String state) {
        if (!users.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + userId + " не существует");
        }
        if (!items.existsItemByOwnerId(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "У пользователя с id=" + userId + " нет зарегестрированых вещей");
        } else {
            return getListBookings(state, userId, true);
        }

    }

    private BookingListDto getListBookings(String state, Long userId, Boolean isOwner) {
        List<Long> itemsId;
        switch (State.fromValue(state.toUpperCase())) {
            case ALL:
                if (isOwner) {
                    itemsId = items.findAllItemIdByOwnerId(userId);
                    return BookingListDto.builder()
                            .bookings(bookings.findAllByItemIdInOrderByStartDesc(itemsId).stream()
                                    .map(mapper::mapToBookingDtoResponse).collect(Collectors.toList())).build();
                } else {
                    return BookingListDto.builder()
                            .bookings(bookings.findAllByBookerIdOrderByStartDesc(userId).stream()
                                    .map(mapper::mapToBookingDtoResponse).collect(Collectors.toList())).build();
                }
            case CURRENT:
                if (isOwner) {
                    itemsId = items.findAllItemIdByOwnerId(userId);
                    return BookingListDto.builder().bookings(
                            bookings.findAllByItemIdInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                                            itemsId, LocalDateTime.now(),LocalDateTime.now()).stream()
                                    .map(mapper::mapToBookingDtoResponse).collect(Collectors.toList())).build();
                } else {
                    return BookingListDto.builder().bookings(
                            bookings.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                                            userId, LocalDateTime.now(),LocalDateTime.now()).stream()
                                    .map(mapper::mapToBookingDtoResponse).collect(Collectors.toList())).build();
                }
            case PAST:
                if (isOwner) {
                    itemsId = items.findAllItemIdByOwnerId(userId);
                    return BookingListDto.builder()
                            .bookings(bookings
                                    .findAllByItemIdInAndEndIsBeforeOrderByStartDesc(itemsId, LocalDateTime.now())
                                    .stream().map(mapper::mapToBookingDtoResponse).collect(Collectors.toList()))
                            .build();
                } else {
                    return BookingListDto.builder()
                            .bookings(bookings
                                    .findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now())
                                    .stream().map(mapper::mapToBookingDtoResponse).collect(Collectors.toList()))
                            .build();
                }
            case FUTURE:
                if (isOwner) {
                    itemsId = items.findAllItemIdByOwnerId(userId);
                    return BookingListDto.builder()
                            .bookings(bookings
                                    .findAllByItemIdInAndStartIsAfterOrderByStartDesc(itemsId, LocalDateTime.now())
                                    .stream().map(mapper::mapToBookingDtoResponse).collect(Collectors.toList()))
                            .build();
                } else {
                    return BookingListDto.builder()
                            .bookings(bookings
                                    .findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now())
                                    .stream().map(mapper::mapToBookingDtoResponse).collect(Collectors.toList()))
                            .build();
                }
            case WAITING:
                if (isOwner) {
                    itemsId = items.findAllItemIdByOwnerId(userId);
                    return BookingListDto.builder()
                            .bookings(bookings
                                    .findAllByItemIdInAndStatusIsOrderByStartDesc(itemsId, Status.WAITING)
                                    .stream().map(mapper::mapToBookingDtoResponse).collect(Collectors.toList()))
                            .build();
                } else {
                    return BookingListDto.builder()
                            .bookings(bookings
                                    .findAllByBookerIdAndStatusIsOrderByStartDesc(userId, Status.WAITING)
                                    .stream().map(mapper::mapToBookingDtoResponse).collect(Collectors.toList()))
                            .build();
                }
            case REJECTED:
                if (isOwner) {
                    itemsId = items.findAllItemIdByOwnerId(userId);
                    return BookingListDto.builder()
                            .bookings(bookings
                                    .findAllByItemIdInAndStatusIsOrderByStartDesc(itemsId, Status.REJECTED)
                                    .stream().map(mapper::mapToBookingDtoResponse).collect(Collectors.toList()))
                            .build();
                } else {
                    return BookingListDto.builder()
                            .bookings(bookings
                                    .findAllByBookerIdAndStatusIsOrderByStartDesc(userId, Status.REJECTED)
                                    .stream().map(mapper::mapToBookingDtoResponse).collect(Collectors.toList()))
                            .build();
                }
            default: throw new StateException("Unknown state: " + state);
        }
    }
}
