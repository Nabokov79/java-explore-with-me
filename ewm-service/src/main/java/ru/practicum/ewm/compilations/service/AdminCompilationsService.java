package ru.practicum.ewm.compilations.service;

import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.NewCompilationDto;

public interface AdminCompilationsService {

    CompilationDto add(NewCompilationDto compilation);

    void deleteById(Long compId);

    void deleteEvent(Long compId,Long eventId);

    void addEvent(Long compId, Long eventId);

    void unpin(Long compId);

    void pin(Long compId);
}
