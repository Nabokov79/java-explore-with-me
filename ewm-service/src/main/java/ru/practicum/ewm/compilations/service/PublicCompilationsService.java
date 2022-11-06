package ru.practicum.ewm.compilations.service;

import ru.practicum.ewm.compilations.dto.CompilationDto;
import java.util.List;

public interface PublicCompilationsService {

    List<CompilationDto> getAll(Boolean pinned, int from, int size);

    CompilationDto getById(Long compId);
}
