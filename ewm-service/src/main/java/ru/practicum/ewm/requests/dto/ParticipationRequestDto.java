package ru.practicum.ewm.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ParticipationRequestDto {

    private Long id;
    private Long requester;
    private Long event;
    private String status;
    private String created;
}
