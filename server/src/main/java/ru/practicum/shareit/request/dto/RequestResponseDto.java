package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.util.DateTimeFormats;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestResponseDto {
    private Integer id;

    private String description;

    @JsonFormat(pattern = DateTimeFormats.DATE_TIME_PATTERN, timezone = DateTimeFormats.TIME_ZONE)
    private LocalDateTime created;

    private List<ItemRequestDto> items;
}