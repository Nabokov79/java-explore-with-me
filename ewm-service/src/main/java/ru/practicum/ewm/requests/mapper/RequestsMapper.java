package ru.practicum.ewm.requests.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.model.Status;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestsMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return new ParticipationRequestDto(request.getId(),
                                            request.getCreated().format(DATE_TIME_FORMATTER),
                                            request.getEvent() != null ? request.getEvent().getId() : null,
                                            request.getRequester() != null ? request.getRequester().getId() : null,
                                            request.getStatus().toString());
    }

    public static Request toRequest() {
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setStatus(Status.PENDING);
        return request;
    }

    public static List<ParticipationRequestDto> toListDto(List<Request> requests) {
        return requests.stream().map(RequestsMapper::toParticipationRequestDto).collect(Collectors.toList());
    }
}