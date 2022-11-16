package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class EndpointHitDto {

    private String app;

    private List<String> uri;

    private String ip;

    private String timestamp;
}
