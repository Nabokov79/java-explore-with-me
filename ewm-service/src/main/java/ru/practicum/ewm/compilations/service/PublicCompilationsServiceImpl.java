package ru.practicum.ewm.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.compilations.mapper.CompilationMapper;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.compilations.repository.CompilationsRepository;
import ru.practicum.ewm.exeption.BadRequestException;
import ru.practicum.ewm.exeption.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationsServiceImpl implements PublicCompilationsService {

    private final CompilationsRepository repository;

    @Override
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size,size);
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = repository.findAll(pageable).getContent();
        } else {
            compilations = repository.findAllByPinned(pinned,pageable);
        }
        if (compilations.isEmpty()) {
            throw new BadRequestException(String.format("Compilation with pinned=%s not found", pinned));
        }
        log.info("Get all compilations with parameter pinned={}", pinned);
        return CompilationMapper.toListDto(compilations);
    }

    @Override
    public CompilationDto getById(Long compId) {
        Compilation compilation = repository.findById(compId)
                  .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%s not found", compId)));
        log.info("Get compilations by compId={}", compId);
            return CompilationMapper.toCompilationDto(compilation);
    }
}
