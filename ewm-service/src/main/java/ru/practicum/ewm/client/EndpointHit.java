package ru.practicum.ewm.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class EndpointHit {

    private String app;

    private String uri;

    private String ip;

    private String timestamp;
}