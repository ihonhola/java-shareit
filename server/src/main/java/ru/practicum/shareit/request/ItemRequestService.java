package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestResponseDto;

import java.util.List;

public interface ItemRequestService {
    RequestResponseDto createRequest(RequestDto requestDto, Integer userId);

    List<RequestResponseDto> getOwnRequests(Integer userId);

    List<RequestResponseDto> getAllOtherUsersRequests(Integer userId, int from, int size);

    RequestResponseDto getRequestById(Integer requestId, Integer userId);
}