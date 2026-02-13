package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingRequestDtoTest {

    @Test
    void testGettersAndSetters() {
        BookingRequestDto dto = new BookingRequestDto();
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        dto.setStart(start);
        dto.setEnd(end);
        dto.setItemId(1);

        assertEquals(start, dto.getStart());
        assertEquals(end, dto.getEnd());
        assertEquals(1, dto.getItemId());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 3, 2, 12, 0);

        BookingRequestDto dto1 = new BookingRequestDto();
        dto1.setStart(start);
        dto1.setEnd(end);
        dto1.setItemId(1);

        BookingRequestDto dto2 = new BookingRequestDto();
        dto2.setStart(start);
        dto2.setEnd(end);
        dto2.setItemId(1);

        BookingRequestDto dto3 = new BookingRequestDto();
        dto3.setStart(start);
        dto3.setEnd(end);
        dto3.setItemId(2);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testToString() {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setStart(LocalDateTime.now());
        dto.setEnd(LocalDateTime.now().plusDays(1));
        dto.setItemId(1);
        assertNotNull(dto.toString());
        assertTrue(dto.toString().contains("itemId=1"));
    }
}