package ru.practicum.shareit.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void handleNotFoundException() {
        NotFoundException ex = new NotFoundException("User not found");
        Map<String, String> response = errorHandler.handleNotFoundException(ex);
        assertEquals("User not found", response.get("Объект не найден"));
    }

    @Test
    void handleOtherExceptions() {
        Exception ex = new RuntimeException("Unexpected error");
        Map<String, String> response = errorHandler.handleOtherExceptions(ex);
        assertEquals("Произошла внутренняя ошибка сервера", response.get("error"));
    }

    @Test
    void handleConflictException() {
        ConflictException ex = new ConflictException("Email already exists");
        Map<String, String> response = errorHandler.handleConflictException(ex);
        assertEquals("Email already exists", response.get("error"));
    }

    @Test
    void handleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        Map<String, String> response = errorHandler.handleIllegalArgumentException(ex);
        assertEquals("Invalid argument", response.get("error"));
    }

    @Test
    void handleValidationException() {
        ValidationException ex = new ValidationException("Validation failed");
        Map<String, String> response = errorHandler.handleValidationException(ex);
        assertEquals("Validation failed", response.get("error"));
    }

    @Test
    void handleAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");
        Map<String, String> response = errorHandler.handleAccessDeniedException(ex);
        assertEquals("Access denied", response.get("error"));
    }

    @Test
    void handleMethodArgumentNotValidException_withFieldError() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "must not be null");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));

        Map<String, String> response = errorHandler.handleMethodArgumentNotValidException(ex);
        assertEquals("Поле 'field': must not be null", response.get("error"));
    }

    @Test
    void handleMethodArgumentNotValidException_withoutFieldError() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Collections.emptyList());

        Map<String, String> response = errorHandler.handleMethodArgumentNotValidException(ex);
        assertEquals("Ошибка валидации", response.get("error"));
    }

    @Test
    void handleConstraintViolationException_withViolation() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("param");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must be positive");
        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        Map<String, String> response = errorHandler.handleConstraintViolationException(ex);
        assertEquals("Параметр 'param': must be positive", response.get("error"));
    }

    @Test
    void handleConstraintViolationException_withoutViolation() {
        ConstraintViolationException ex = new ConstraintViolationException(Set.of());
        Map<String, String> response = errorHandler.handleConstraintViolationException(ex);
        assertEquals("Ошибка валидации параметров", response.get("error"));
    }
}