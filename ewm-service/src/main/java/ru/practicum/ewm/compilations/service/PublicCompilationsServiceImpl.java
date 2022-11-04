package ru.practicum.ewm.compilations.service;

import ru.practicum.ewm.compilations.mapper.CompilationMapper;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.compilations.repository.CompilationsRepository;
import ru.practicum.ewm.exeption.BadRequestException;
import ru.practicum.ewm.exeption.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilations.dto.CompilationDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicCompilationsServiceImpl implements PublicCompilationsService {

    private final CompilationsRepository repository;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public PublicCompilationsServiceImpl(CompilationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size,size);
        List<Compilation> compilationList;
        logger.info("Get all compilations with parameter pinned={}", pinned);
        if (pinned == null) {
            compilationList = repository.findAll(pageable).getContent();
        } else {
            compilationList = repository.findAllByPinned(pinned,pageable);
        }
        if (compilationList.isEmpty()) {
            throw new BadRequestException(String.format("Compilation with pinned=%s not found", pinned));
        }
        return compilationList.stream().map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = repository.findById(compId)
                  .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%s not found", compId)));
            return CompilationMapper.toCompilationDto(compilation);
    }
}
