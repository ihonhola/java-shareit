package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.util.DateTimeFormats;

import java.time.LocalDateTime;

@Data
public class CommentResponseDto {

    private Integer id;

    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;

    private String authorName;

    @JsonFormat(pattern = DateTimeFormats.DATE_TIME_PATTERN, timezone = DateTimeFormats.TIME_ZONE)
    private LocalDateTime created;
}