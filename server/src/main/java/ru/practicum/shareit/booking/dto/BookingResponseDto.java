package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.DateTimeFormats;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingResponseDto {
    private Integer id;

    @JsonFormat(pattern = DateTimeFormats.DATE_TIME_PATTERN, timezone = DateTimeFormats.TIME_ZONE)
    private LocalDateTime start;

    @JsonFormat(pattern = DateTimeFormats.DATE_TIME_PATTERN, timezone = DateTimeFormats.TIME_ZONE)
    private LocalDateTime end;

    private Integer itemId;

    private Integer bookerId;

    private String status;

    private ItemDto item;

    private UserDto booker;
}
