package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Integer ownerId);

    ItemDto updateItem(Integer itemId, ItemDto itemDto, Integer ownerId);

    ItemDto getItemById(Integer itemId, Integer userId);

    List<ItemDto> getAllItemsByOwner(Integer ownerId);

    List<ItemDto> searchAvailableItems(String text);
}