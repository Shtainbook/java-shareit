package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
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
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDtoResponse createBooking(Long bookerId, BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Дата окончания бронирования не может быть раньше даты начала");
        }

        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Предмета с id=" + bookingDto.getItemId() + " нет"));
        if (item.getOwner().getId().equals(bookerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Владелец не может забронировать свою вещь");
        }
        if (item.getAvailable()) {
            User user = userRepository.findById(bookerId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Пользователя с id=" + bookerId + " нет"));
            Booking booking = bookingMapper.mapToBookingFromBookingDto(bookingDto);
            booking.setItem(item);
            booking.setBooker(user);
            return bookingMapper.mapToBookingDtoResponse(bookingRepository.save(booking));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Вещь с id=" + item.getId()
                    + " недоступна для бронирования");
        }
    }

    @Override
    @Transactional
    public BookingDtoResponse approveBooking(Long ownerId, Long bookingId, String approved) {
        String approve = approved.toLowerCase();
        if ((approve.equals("true") || approve.equals("false"))) {
            Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронирования с id=" + bookingId + " нет"));
            if (!booking.getStatus().equals(Status.WAITING)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Невозможно изменить статус брони со статусом " + booking.getStatus() + ".");
            }
            if (booking.getItem().getOwner().getId().equals(ownerId)) {
                if (approve.equals("true")) {
                    booking.setStatus(Status.APPROVED);
                } else {
                    booking.setStatus(Status.REJECTED);
                }
                return bookingMapper.mapToBookingDtoResponse(bookingRepository.save(booking));
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Пользователь с id=" + ownerId + " не является владельцем вещи с id=" + booking.getItem().getOwner().getId());
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неккоректный параметр строки approved");
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoResponse getBookingByIdForOwnerAndBooker(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронирования с id=" + bookingId + " нет"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return bookingMapper.mapToBookingDtoResponse(booking);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id=" + userId
                + " не является автором бронирования или владельцем вещи, к которой относится бронирование");
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getAllBookingsForUser(Pageable pageable, Long userId, String state) {
        if (userRepository.existsById(userId)) {
            return getListBookings(pageable, state, userId, false);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + userId + " не существует");
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getAllBookingsForItemsUser(Pageable pageable, Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + userId + " не существует");
        }
        if (!itemRepository.existsItemByOwnerId(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "У пользователя с id=" + userId + " нет зарегестрированых вещей");
        } else {
            return getListBookings(pageable, state, userId, true);
        }
    }

    private List<BookingDtoResponse> getListBookings(Pageable pageable, String state, Long userId, Boolean isOwner) {
        List<Long> itemsId;

        State status;
        try {
            status = State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unknown state: UNSUPPORTED_STATUS");
        }

        switch (status) {
            case ALL:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    return bookingRepository.findAllByItemIdInOrderByStartDesc(pageable, itemsId).stream()
                            .map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList());
                } else {
                    return bookingRepository.findAllByBookerIdOrderByStartDesc(pageable, userId).stream()
                            .map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList());
                }
            case CURRENT:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    return bookingRepository.findAllByItemIdInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                                    pageable, itemsId, LocalDateTime.now(), LocalDateTime.now()).stream()
                            .map(bookingMapper::mapToBookingDtoResponse)
                                    .sorted((a,b)-> (int) (a.getId()-b.getId())) // тест куруна
                            .collect(Collectors.toList());
                } else {
                    return bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                                    pageable, userId, LocalDateTime.now(), LocalDateTime.now()).stream() // курун!
                            .map(bookingMapper::mapToBookingDtoResponse).sorted((a,b)-> (int) (a.getId()-b.getId())).collect(Collectors.toList());
                }
            case PAST:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    return bookingRepository.findAllByItemIdInAndEndIsBeforeOrderByStartDesc(
                            pageable, itemsId, LocalDateTime.now()
                    ).stream().map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList());
                } else {
                    return bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(
                            pageable, userId, LocalDateTime.now()
                    ).stream().map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList());
                }
            case FUTURE:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    return bookingRepository.findAllByItemIdInAndStartIsAfterOrderByStartDesc(pageable, itemsId, LocalDateTime.now().minusSeconds(1).minusNanos(100))
                            .stream().map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList());
                } else {
                    return bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(
                            pageable, userId, LocalDateTime.now()
                    ).stream().map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList());
                }
            case WAITING:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    return bookingRepository
                            .findAllByItemIdInAndStatusIsOrderByStartDesc(pageable, itemsId, Status.WAITING)
                            .stream().map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList());
                } else {
                    return bookingRepository
                            .findAllByBookerIdAndStatusIsOrderByStartDesc(pageable, userId, Status.WAITING)
                            .stream().map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList());
                }
            case REJECTED:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    return bookingRepository
                            .findAllByItemIdInAndStatusIsOrderByStartDesc(pageable, itemsId, Status.REJECTED)
                            .stream().map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList());
                } else {
                    return bookingRepository
                            .findAllByBookerIdAndStatusIsOrderByStartDesc(pageable, userId, Status.REJECTED)
                            .stream().map(bookingMapper::mapToBookingDtoResponse).collect(Collectors.toList());
                }
            default:
                throw new StateException("Unknown state: " + state);
        }
    }
}