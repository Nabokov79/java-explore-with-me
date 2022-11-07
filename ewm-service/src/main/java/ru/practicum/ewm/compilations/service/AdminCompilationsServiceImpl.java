package ru.practicum.ewm.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.compilations.mapper.CompilationMapper;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.compilations.repository.CompilationsRepository;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventsRepository;
import ru.practicum.ewm.exeption.NotFoundException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilations.dto.CompilationDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCompilationsServiceImpl implements AdminCompilationsService {

    private final CompilationsRepository repository;
    private final EventsRepository eventsRepository;
    private final PublicCompilationsService publicCompilationsService;

    @Override
    public CompilationDto add(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        compilation.setEvents(eventsRepository.findAllById(newCompilationDto.getEvents()));
        log.info("Save new compilation compilation = " + compilation);
        return CompilationMapper.toCompilationDto(repository.save(compilation));
    }

    @Override
    public void deleteById(Long compId) {
        publicCompilationsService.getById(compId);
        repository.deleteById(compId);
        log.info("Delete compilation by compId={}", compId);
    }

    @Override
    public void deleteEvent(Long compId, Long eventId) {
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%s not found", compId)));
        compilation.setEvents(compilation.getEvents().stream().filter(event -> event.getId() != eventId)
                                                                      .collect(Collectors.toList()));
        repository.save(compilation);
        log.info("Event delete with eventId={}", eventId);
    }

    @Override
    public void addEvent(Long compId, Long eventId) {
        Compilation compilation = repository.findById(compId)
                   .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%s not found", compId)));
        List<Event> eventList = compilation.getEvents();
        Event event = eventsRepository.findById(eventId)
                         .orElseThrow(() ->  new NotFoundException(String.format("Event not found by id=%s", eventId)));
        eventList.add(event);
        repository.save(compilation);
    }

    @Override
    public void unpin(Long compId) {
        repository.save(setPinned(compId, false));
        log.info("Compilation unpinned compId={}", compId);
    }

    @Override
    public void pin(Long compId) {
        repository.save(setPinned(compId, true));
        log.info("Compilation pinned compId={}", compId);
    }

    private Compilation getCompilationById(Long compId) {
        log.info("Get compilation by compId={}", compId);
        return repository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found by campId=" + compId));
    }

    private Compilation setPinned(Long compId, boolean pinned) {
        Compilation compilation = getCompilationById(compId);
        compilation.setPinned(pinned);
        return compilation;
    }
}
