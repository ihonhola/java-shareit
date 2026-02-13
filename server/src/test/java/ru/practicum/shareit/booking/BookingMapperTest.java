package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookingMapperTest {

    @Test
    void toBookingResponseDto_shouldMapCorrectly() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.APPROVED);

        Item item = new Item();
        item.setId(10);
        booking.setItem(item);

        User booker = new User();
        booker.setId(20);
        booking.setBooker(booker);

        BookingResponseDto dto = BookingMapper.toBookingResponseDto(booking);
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals(10, dto.getItemId());
        assertEquals(20, dto.getBookerId());
        assertEquals("APPROVED", dto.getStatus());
    }

    @Test
    void toBookingResponseDto_shouldReturnNull_whenBookingNull() {
        assertNull(BookingMapper.toBookingResponseDto(null));
    }

    @Test
    void toBooking_shouldMapCorrectly() {
        BookingRequestDto request = new BookingRequestDto();
        request.setStart(LocalDateTime.now().plusHours(1));
        request.setEnd(LocalDateTime.now().plusDays(1));
        request.setItemId(100);

        Item item = new Item();
        item.setId(100);
        User booker = new User();
        booker.setId(200);

        Booking booking = BookingMapper.toBooking(request, item, booker);
        assertNotNull(booking);
        assertEquals(request.getStart(), booking.getStart());
        assertEquals(request.getEnd(), booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void toBooking_shouldReturnNull_whenRequestNull() {
        assertNull(BookingMapper.toBooking(null, new Item(), new User()));
    }
}