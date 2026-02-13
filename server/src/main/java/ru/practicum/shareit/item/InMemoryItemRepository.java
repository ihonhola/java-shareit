package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Deprecated
@Repository
public class InMemoryItemRepository {
    private final Map<Integer, Item> items = new HashMap<>();

    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(getNextId());
        }
        items.put(item.getId(), item);
        return item;
    }

    public Item update(Item item) {
        Integer itemId = item.getId();

        if (itemId == null || !items.containsKey(itemId)) {
            throw new NotFoundException("Вещь с ID " + itemId + " не найдена");
        }

        Item existingItem = items.get(itemId);

        existingItem.setName(item.getName());
        existingItem.setDescription(item.getDescription());
        existingItem.setAvailable(item.getAvailable());

        items.put(itemId, existingItem);
        return existingItem;
    }

    public Optional<Item> findById(Integer id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<Item> findAllByOwner(User owner) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(owner))
                .collect(Collectors.toList());
    }

    public List<Item> searchAvailableItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String searchText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        items.remove(id);
    }

    public boolean existsById(Integer id) {
        return items.containsKey(id);
    }

    private int getNextId() {
        return items.keySet().stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
