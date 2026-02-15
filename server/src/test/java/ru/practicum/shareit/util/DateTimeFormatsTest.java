package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DateTimeFormatsTest {

    @Test
    void constantsShouldBeDefined() {
        assertNotNull(DateTimeFormats.DATE_TIME_PATTERN);
        assertNotNull(DateTimeFormats.TIME_ZONE);
        assertEquals("yyyy-MM-dd'T'HH:mm:ss", DateTimeFormats.DATE_TIME_PATTERN);
        assertEquals("UTC", DateTimeFormats.TIME_ZONE);
    }
}