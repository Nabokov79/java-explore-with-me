package ru.practicum.ewm.requests.mapper;

import ru.practicum.ewm.paramRequest.Param;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.model.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RequestsMapper {

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return new ParticipationRequestDto(request.getId(),
                                            request.getCreated().format(Param.DATE_TIME_FORMATTER),
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
}