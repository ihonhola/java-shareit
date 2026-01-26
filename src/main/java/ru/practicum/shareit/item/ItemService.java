package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, int ownerId);

    ItemDto updateItem(int itemId, ItemDto itemDto, int ownerId);

    ItemDto getItemById(int itemId, int userId);

    List<ItemDto> getAllItemsByOwner(int ownerId);

    List<ItemDto> searchAvailableItems(String text);
}