package ru.practicum.shareit.booking.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
class BookItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<BookItemRequestDto> json;

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testSerialization() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 3, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 3, 2, 12, 0, 0);
        BookItemRequestDto dto = new BookItemRequestDto(1, start, end);

        var jsonContent = json.write(dto);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.start").isEqualTo("2026-03-01T10:00:00");
        assertThat(jsonContent).extractingJsonPathStringValue("$.end").isEqualTo("2026-03-02T12:00:00");
    }

    @Test
    void testDeserialization() throws Exception {
        String content = """
                {
                    "itemId": 2,
                    "start": "2026-04-01T11:00:00",
                    "end": "2026-04-02T13:00:00"
                }
                """;
        BookItemRequestDto dto = json.parseObject(content);
        assertThat(dto.getItemId()).isEqualTo(2);
        assertThat(dto.getStart()).isEqualTo("2026-04-01T11:00:00");
        assertThat(dto.getEnd()).isEqualTo("2026-04-02T13:00:00");
    }

    @Test
    void validation_shouldFailWhenFieldsAreNull() {
        BookItemRequestDto dto = new BookItemRequestDto(null, null, null);
        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("itemId"));
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("start"));
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("end"));
    }

    @Test
    void validation_shouldFailWhenStartInPast() {
        LocalDateTime past = LocalDateTime.now().minusDays(1);
        BookItemRequestDto dto = new BookItemRequestDto(100, past, LocalDateTime.now().plusDays(1));
        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("start"));
    }

    @Test
    void validation_shouldFailWhenEndNotFuture() {
        LocalDateTime now = LocalDateTime.now();
        BookItemRequestDto dto = new BookItemRequestDto(100, now.plusDays(1), now.minusDays(1));
        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("end"));
    }

    @Test
    void validation_shouldPassWhenAllValid() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookItemRequestDto dto = new BookItemRequestDto(100, start, end);
        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}