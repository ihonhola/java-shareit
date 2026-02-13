package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestResponseDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestResponseDto createRequest(
            @RequestBody RequestDto requestDto,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {

        log.info("Запрос на создание запроса вещи от пользователя ID: {}", userId);
        return itemRequestService.createRequest(requestDto, userId);
    }

    @GetMapping
    public List<RequestResponseDto> getOwnRequests(
            @RequestHeader("X-Sharer-User-Id") Integer userId) {

        log.info("Запрос на получение своих запросов от пользователя ID: {}", userId);
        return itemRequestService.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestResponseDto> getAllOtherUsersRequests(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Запрос на получение запросов других пользователей от пользователя ID: {}, from={}, size={}",
                userId, from, size);
        return itemRequestService.getAllOtherUsersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestResponseDto getRequestById(
            @PathVariable Integer requestId,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {

        log.info("Запрос на получение запроса ID: {} от пользователя ID: {}", requestId, userId);
        return itemRequestService.getRequestById(requestId, userId);
    }
}
