package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;
import java.util.List;

public interface StatService {

    void saveStat(EndpointHit endpointHit);

    List<ViewStats> getAllStats(String start, String end, List<String> uris, Boolean unique);
}