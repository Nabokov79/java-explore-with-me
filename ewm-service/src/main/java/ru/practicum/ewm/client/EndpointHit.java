package ru.practicum.ewm.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHit {

    private String id;

    private String app;

    private String uri;

    private String ip;

    private String timestamp;
}