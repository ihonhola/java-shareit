package ru.practicum.shareit.request.dto;

import lombok.Data;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    private Integer id;

    private String name;

    private Integer ownerId;

    private Boolean available;

    private String description;

    private Integer requestId;

    //private LocalDateTime created;

    //private List<ItemDto> items;
}
