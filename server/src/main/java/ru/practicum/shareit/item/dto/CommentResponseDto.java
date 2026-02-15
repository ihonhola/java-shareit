package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.util.DateTimeFormats;

import java.time.LocalDateTime;

@Data
public class CommentResponseDto {

    private Integer id;

    private String text;

    private String authorName;

    @JsonFormat(pattern = DateTimeFormats.DATE_TIME_PATTERN, timezone = DateTimeFormats.TIME_ZONE)
    private LocalDateTime created;
}