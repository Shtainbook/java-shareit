package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.error.handler.exception.StateException;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    private final BookingService bookingService;
    private static BookingDto bookingDto;
    private static ItemShortDto itemShortDto;
    private static UserShortDto userShortDto;
    private List<BookingDtoResponse> bookingListDto;
    private static BookingDtoResponse bookingDtoResponse;

    @BeforeAll
    public static void setUp() {
        bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        itemShortDto = ItemShortDto.builder()
                .id(bookingDto.getItemId())
                .name("test item")
                .build();
        userShortDto = UserShortDto.builder()
                .id(1L)
                .name("test name")
                .build();
        bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(itemShortDto)
                .booker(userShortDto)
                .status(bookingDto.getStatus())
                .build();
    }

    @Test
    @SneakyThrows
    public void createBooking() {
        //when
        when(bookingService.createBooking(anyLong(), any(BookingDto.class))).thenReturn(bookingDtoResponse);
        mvc.perform(
                        post("/bookings")
                                .content(objectMapper.writeValueAsString(bookingDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1))
                .andDo(print())
                //then
                .andExpectAll(
                        status().isCreated(),
                        content().json(objectMapper.writeValueAsString(bookingDtoResponse))
                );
    }


    @Test
    @SneakyThrows
    public void approveBooking() {
        //given
        bookingDtoResponse.setStatus(Status.APPROVED);
        //when
        when(bookingService.approveBooking(anyLong(), anyLong(), anyString())).thenReturn(bookingDtoResponse);
        mvc.perform(
                        (patch("/bookings/1"))
                                .header("X-Sharer-User-Id", 1)
                                .param("approved", "true"))
                .andDo(print())
                //then
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(bookingDtoResponse))
                );
        bookingDtoResponse.setStatus(Status.WAITING);
    }


    @Test
    @SneakyThrows
    public void getBookingByIdForOwnerAndBooker() {
        //when
        when(bookingService.getBookingByIdForOwnerAndBooker(anyLong(), anyLong())).thenReturn(bookingDtoResponse);
        mvc.perform(
                        get("/bookings/1")
                                .header("X-Sharer-User-Id", 1))
                .andDo(print())
                //then
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(bookingDtoResponse))
                );
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForUser() {
        //given
        bookingListDto = List.of(bookingDtoResponse);
        //when
        when(bookingService.getAllBookingsForUser(any(Pageable.class), anyLong(), anyString()))
                .thenReturn(bookingListDto);
        mvc.perform(
                        get("/bookings")
                                .header("X-Sharer-User-Id", 1)
                                .param("from", "0")
                                .param("size", "2"))
                .andDo(print())
                //then
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(bookingListDto))
                );
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForUserWithIncorrectState() {
        //given
        bookingListDto = List.of(bookingDtoResponse);
        //when
        when(bookingService.getAllBookingsForUser(any(Pageable.class), anyLong(), anyString()))
                .thenThrow(StateException.class);
        mvc.perform(
                        get("/bookings")
                                .header("X-Sharer-User-Id", 1)
                                .param("from", "0")
                                .param("size", "2")
                                .param("state", "qwe"))
                .andDo(print())
                //then
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(bookingService, times(1)).getAllBookingsForUser(any(Pageable.class), anyLong(), anyString());
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForItemsUser() {
        //given
        bookingListDto = List.of(bookingDtoResponse);
        //when
        when(bookingService.getAllBookingsForItemsUser(any(Pageable.class), anyLong(), anyString()))
                .thenReturn(bookingListDto);
        mvc.perform(
                        get("/bookings/owner")
                                .header("X-Sharer-User-Id", 1)
                                .param("from", "0")
                                .param("size", "2"))
                .andDo(print())
                //then
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(bookingListDto))
                );
    }
}
