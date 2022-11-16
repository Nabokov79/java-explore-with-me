package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStats;
import java.util.List;

public interface StatService {

    void saveStat(EndpointHitDto endpointHit);

    List<ViewStats> getAllStats(String start, String end, List<String> uris, Boolean unique);
}
