package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingRequestDto> jsonRequest;

    @Autowired
    private JacksonTester<BookingResponseDto> jsonResponse;

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testBookingRequestDtoSerialization() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 3, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 3, 2, 12, 0, 0);

        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(1);
        dto.setStart(start);
        dto.setEnd(end);

        JsonContent<BookingRequestDto> json = jsonRequest.write(dto);
        assertThat(json).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.start").isEqualTo("2026-03-01T10:00:00");
        assertThat(json).extractingJsonPathStringValue("$.end").isEqualTo("2026-03-02T12:00:00");
    }

    @Test
    void testBookingResponseDtoSerialization() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 3, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 3, 2, 12, 0, 0);

        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(1);
        dto.setStart(start);
        dto.setEnd(end);
        dto.setStatus("WAITING");
        dto.setItemId(10);
        dto.setBookerId(20);

        JsonContent<BookingResponseDto> json = jsonResponse.write(dto);
        assertThat(json).extractingJsonPathStringValue("$.start").isEqualTo("2026-03-01T10:00:00");
        assertThat(json).extractingJsonPathStringValue("$.end").isEqualTo("2026-03-02T12:00:00");
        assertThat(json).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}