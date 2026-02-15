package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionsTest {

    @Test
    void testNotFoundException() {
        NotFoundException ex = new NotFoundException("Not found");
        assertEquals("Not found", ex.getMessage());
    }

    @Test
    void testValidationException() {
        ValidationException ex = new ValidationException("Validation error");
        assertEquals("Validation error", ex.getMessage());
    }

    @Test
    void testConflictException() {
        ConflictException ex = new ConflictException("Conflict");
        assertEquals("Conflict", ex.getMessage());
    }

    @Test
    void testAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");
        assertEquals("Access denied", ex.getMessage());
    }
}