package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EndpointHitDto {

    private String id;

    private String app;

    private String uri;

    private String ip;

    private String timestamp;
}
