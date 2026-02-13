package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookingStatusTest {

    @Test
    void testValues() {
        assertNotNull(BookingStatus.valueOf("WAITING"));
        assertNotNull(BookingStatus.valueOf("APPROVED"));
        assertNotNull(BookingStatus.valueOf("REJECTED"));
        assertNotNull(BookingStatus.valueOf("CANCELED"));
    }
}