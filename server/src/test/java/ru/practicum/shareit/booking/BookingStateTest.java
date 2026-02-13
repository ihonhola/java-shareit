package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookingStateTest {

    @Test
    void from_shouldReturnCorrectEnum() {
        assertEquals(BookingState.ALL, BookingState.from("ALL"));
        assertEquals(BookingState.CURRENT, BookingState.from("CURRENT"));
        assertEquals(BookingState.PAST, BookingState.from("PAST"));
        assertEquals(BookingState.FUTURE, BookingState.from("FUTURE"));
        assertEquals(BookingState.WAITING, BookingState.from("WAITING"));
        assertEquals(BookingState.REJECTED, BookingState.from("REJECTED"));
    }

    @Test
    void from_shouldThrowException_whenInvalidState() {
        assertThrows(IllegalArgumentException.class, () -> BookingState.from("INVALID"));
    }

    @Test
    void from_shouldReturnAll_whenNullOrBlank() {
        assertEquals(BookingState.ALL, BookingState.from(null));
        assertEquals(BookingState.ALL, BookingState.from(" "));
    }
}