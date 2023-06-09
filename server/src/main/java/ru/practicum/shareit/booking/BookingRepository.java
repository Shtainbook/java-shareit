package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends PagingAndSortingRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Pageable pageable, Long bookerId);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Pageable pageable, Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(
            Pageable pageable, Long bookerId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(
            Pageable pageable, Long bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatusIsOrderByStartDesc(Pageable pageable, Long bookerId, Status status);

    List<Booking> findAllByItemIdInOrderByStartDesc(Pageable pageable, Collection<Long> itemId);

    List<Booking> findAllByItemIdInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Pageable pageable, Collection<Long> itemId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemIdInAndEndIsBeforeOrderByStartDesc(
            Pageable pageable, Collection<Long> itemId, LocalDateTime end);

    List<Booking> findAllByItemIdInAndStartIsAfterOrderByStartDesc(
            Pageable pageable, Collection<Long> itemId, LocalDateTime start);

    List<Booking> findAllByItemIdInAndStatusIsOrderByStartDesc(
            Pageable pageable, Collection<Long> itemId, Status status);

    Booking findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime end, Status status);

    Booking findFirstByItemIdAndStartBeforeAndEndAfterAndStatusOrderByEndDesc(
            Long itemId, LocalDateTime start, LocalDateTime end, Status status);

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime start, Status status);

    Boolean existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(
            Long itemId, Long bookerId, Status status, LocalDateTime end);

    List<Booking> findByItemIdInAndEndBeforeAndStatusOrderByEndDesc(
            List<Long> itemsId, LocalDateTime endTime, Status status);

    List<Booking> findByItemIdInAndStartAfterAndStatusOrderByStartAsc(
            List<Long> itemsId, LocalDateTime endTime, Status status);
}