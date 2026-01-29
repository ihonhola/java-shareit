package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, int ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        }

        Item item = ItemMapper.toItem(itemDto, owner, request);

        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(int itemId, ItemDto itemDto, int ownerId) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        // Проверяем, что владелец совпадает
        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Нельзя редактировать чужую вещь");
        }

        ItemMapper.updateItemFromDto(itemDto, existingItem);

        Item updatedItem = itemRepository.save(existingItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(int itemId, int userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        ItemDto itemDto = ItemMapper.toItemDto(item);

        // Добавляем комментарии для всех пользователей
        itemDto.setComments(getCommentsForItem(itemId));

        // Добавляем информацию о бронированиях только для владельца
        if (item.getOwner().getId().equals(userId)) {
            addBookingInfoToItem(itemDto, itemId);
        }

        return itemDto;
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(int ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Item> items = itemRepository.findAllByOwnerOrderById(owner);
        List<Integer> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        // Получаем все бронирования для вещей владельца
        Map<Integer, BookingDto> lastBookings = getLastBookings(itemIds);
        Map<Integer, BookingDto> nextBookings = getNextBookings(itemIds);

        // Получаем все комментарии для вещей владельца
        Map<Integer, List<CommentDto>> commentsByItem = getCommentsByItemIds(itemIds);

        return items.stream()
                .map(item -> {
                    ItemDto itemDto = ItemMapper.toItemDto(item);
                    itemDto.setLastBooking(lastBookings.get(item.getId()));
                    itemDto.setNextBooking(nextBookings.get(item.getId()));
                    itemDto.setComments(commentsByItem.getOrDefault(item.getId(), Collections.emptyList()));
                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.searchAvailableItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Integer itemId, CommentDto commentDto, Integer authorId) {
        // Проверяем существование вещи
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        // Проверяем существование пользователя
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        // Проверяем, что пользователь брал вещь в аренду
        if (!hasUserBookedItem(authorId, itemId)) {
            throw new ValidationException("Пользователь не брал эту вещь в аренду");
        }

        // Создаем комментарий
        Comment comment = CommentMapper.toComment(commentDto, item, author);
        comment.setCreated(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        log.info("Добавлен комментарий ID: {} к вещи ID: {} от пользователя ID: {}",
                savedComment.getId(), itemId, authorId);

        return CommentMapper.toCommentDto(savedComment);
    }

    // Вспомогательные методы
    private void addBookingInfoToItem(ItemDto itemDto, Integer itemId) {
        // Получаем последнее завершенное бронирование
        bookingRepository.findFirstByItemIdAndBookerIdAndEndBeforeAndStatus(
                        itemId, null, LocalDateTime.now(), BookingStatus.APPROVED)
                .ifPresent(booking ->
                        itemDto.setLastBooking(BookingMapper.toBookingDto(booking)));

        // Получаем ближайшее следующее бронирование
        bookingRepository.findAllByItemIdAndStatusNotAndStartAfterOrderByStartAsc(
                        itemId, BookingStatus.REJECTED, LocalDateTime.now())
                .stream()
                .findFirst()
                .ifPresent(booking ->
                        itemDto.setNextBooking(BookingMapper.toBookingDto(booking)));
    }

    private Map<Integer, BookingDto> getLastBookings(List<Integer> itemIds) {
        Map<Integer, BookingDto> result = new HashMap<>();

        for (Integer itemId : itemIds) {
            bookingRepository.findFirstByItemIdAndBookerIdAndEndBeforeAndStatus(
                            itemId, null, LocalDateTime.now(), BookingStatus.APPROVED)
                    .ifPresent(booking ->
                            result.put(itemId, BookingMapper.toBookingDto(booking)));
        }

        return result;
    }

    private Map<Integer, BookingDto> getNextBookings(List<Integer> itemIds) {
        Map<Integer, BookingDto> result = new HashMap<>();

        for (Integer itemId : itemIds) {
            bookingRepository.findAllByItemIdAndStatusNotAndStartAfterOrderByStartAsc(
                            itemId, BookingStatus.REJECTED, LocalDateTime.now())
                    .stream()
                    .findFirst()
                    .ifPresent(booking ->
                            result.put(itemId, BookingMapper.toBookingDto(booking)));
        }

        return result;
    }

    private List<CommentDto> getCommentsForItem(Integer itemId) {
        return commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private Map<Integer, List<CommentDto>> getCommentsByItemIds(List<Integer> itemIds) {
        if (itemIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Integer, List<CommentDto>> result = new HashMap<>();
        List<Comment> comments = commentRepository.findAllByItemIdIn(itemIds);

        for (Comment comment : comments) {
            Integer itemId = comment.getItem().getId();
            result.computeIfAbsent(itemId, k -> new ArrayList<>())
                    .add(CommentMapper.toCommentDto(comment));
        }

        return result;
    }

    private boolean hasUserBookedItem(Integer userId, Integer itemId) {
        return bookingRepository.existsByItemIdAndBookerIdAndEndBeforeAndStatusNotRejected(
                itemId, userId, LocalDateTime.now());
    }
}