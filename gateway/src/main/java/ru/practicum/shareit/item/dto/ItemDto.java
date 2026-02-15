package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ItemDto {
    private Integer id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotNull(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Статус доступности должен быть указан")
    private Boolean available;

    private Integer requestId;

    private BookingResponseDto lastBooking;

    private BookingResponseDto nextBooking;

    private List<CommentResponseDto> comments;
}
