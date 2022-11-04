package ru.practicum.ewm.compilations.service;

import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.NewCompilationDto;

public interface AdminCompilationsService {

    CompilationDto addCompilation(NewCompilationDto compilation);

    void deleteCompilationById(Long compId);

    void deleteEventFromCompilation(Long compId,Long eventId);

    void addEventToCompilation(Long compId, Long eventId);

    void unpinCompilationOnPage(Long compId);

    void pinCompilationOnPage(Long compId);
}
