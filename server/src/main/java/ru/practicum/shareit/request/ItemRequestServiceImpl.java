package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public RequestResponseDto createRequest(RequestDto requestDto, Integer userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDto, requestor);
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);

        log.info("Создан запрос ID: {} пользователем ID: {}", savedRequest.getId(), userId);

        RequestResponseDto responseDto = ItemRequestMapper.toRequestResponseDto(savedRequest);
        // Загружаем связанные вещи для нового запроса (пока пустой список)
        responseDto.setItems(findItemsByRequestId(savedRequest.getId()));

        return responseDto;
    }

    @Override
    public List<RequestResponseDto> getOwnRequests(Integer userId) {
        // Проверяем существование пользователя
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        // Получаем запросы пользователя, отсортированные от новых к старым
        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(userId);

        return requests.stream()
                .map(request -> {
                    RequestResponseDto dto = ItemRequestMapper.toRequestResponseDto(request);
                    dto.setItems(findItemsByRequestId(request.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestResponseDto> getAllOtherUsersRequests(Integer userId, int from, int size) {
        // Проверяем существование пользователя
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        // Настраиваем пагинацию с сортировкой от новых к старым
       // Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        if (from % size != 0) {
            throw new ValidationException("Параметр 'from' должен быть кратен 'size'");
        }
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));

        // Получаем запросы других пользователей
        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorIdNotOrderByCreatedDesc(userId, pageable);

        return requests.stream()
                .map(request -> {
                    RequestResponseDto dto = ItemRequestMapper.toRequestResponseDto(request);
                    dto.setItems(findItemsByRequestId(request.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public RequestResponseDto getRequestById(Integer requestId, Integer userId) {
        // Проверяем существование пользователя
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        // Находим запрос
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        RequestResponseDto responseDto = ItemRequestMapper.toRequestResponseDto(itemRequest);
        responseDto.setItems(findItemsByRequestId(requestId));

        return responseDto;
    }

    private List<ItemDto> findItemsByRequestId(Integer requestId) {
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}