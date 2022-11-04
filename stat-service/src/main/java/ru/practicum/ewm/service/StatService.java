package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStats;
import java.util.List;

public interface StatService {

    void saveStat(EndpointHitDto endpointHitList);

    List<ViewStats> getAllStats(String start, String end, String uris, Boolean unique);
}
