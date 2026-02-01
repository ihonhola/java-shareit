package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Дата начала не может быть пустой")
    @FutureOrPresent(message = "Дата начала должна быть в настоящем или будущем")
    @JsonFormat(pattern = DateTimeFormats.DATE_TIME_PATTERN, timezone = DateTimeFormats.TIME_ZONE)
    private LocalDateTime start;

    @NotNull(message = "Дата окончания не может быть пустой")
    @Future(message = "Дата окончания должна быть в будущем")
    @JsonFormat(pattern = DateTimeFormats.DATE_TIME_PATTERN, timezone = DateTimeFormats.TIME_ZONE)
    private LocalDateTime end;

    @NotNull(message = "ID вещи не может быть пустым")
    private Integer itemId;

    private Integer bookerId;

    private String status;

    private ItemDto item;

    private UserDto booker;
}
