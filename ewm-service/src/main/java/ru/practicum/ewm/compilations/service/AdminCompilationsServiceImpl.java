package ru.practicum.ewm.compilations.service;

import ru.practicum.ewm.compilations.mapper.CompilationMapper;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.compilations.repository.CompilationsRepository;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventsRepository;
import ru.practicum.ewm.exeption.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.NewCompilationDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminCompilationsServiceImpl implements AdminCompilationsService {

    private final CompilationsRepository repository;
    private final EventsRepository eventsRepository;
    private final PublicCompilationsService publicCompilationsService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AdminCompilationsServiceImpl(CompilationsRepository repository,
                                        EventsRepository eventsRepository,
                                        PublicCompilationsService publicCompilationsService) {
        this.repository = repository;
        this.eventsRepository = eventsRepository;
        this.publicCompilationsService = publicCompilationsService;
    }

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        compilation.setEventsList(eventsRepository.findAllById(newCompilationDto.getEvents()));
        logger.info("Save new compilation compilation = " + compilation);
        return CompilationMapper.toCompilationDto(repository.save(compilation));
    }

    @Override
    public void deleteCompilationById(Long compId) {
        publicCompilationsService.getCompilationById(compId);
        repository.deleteById(compId);
        logger.info("Delete compilation by compId={}", compId);
    }

    @Override
    public void deleteEventFromCompilation(Long compId, Long eventId) {
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%s not found", compId)));
        compilation.setEventsList(compilation.getEventsList().stream()
                                                             .filter(event -> event.getId() != eventId)
                                                             .collect(Collectors.toList()));
        repository.save(compilation);
        logger.info("Event delete with eventId={}", eventId);
    }

    @Override
    public void addEventToCompilation(Long compId, Long eventId) {
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%s not found", compId)));
        List<Event> eventList = compilation.getEventsList();
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() ->  new NotFoundException(String.format("Event not found by id=%s", eventId)));
        eventList.add(event);
        repository.save(compilation);
    }

    @Override
    public void unpinCompilationOnPage(Long compId) {
        Compilation compilation = getCompilationById(compId);
        compilation.setPinned(false);
        repository.save(compilation);
        logger.info("Compilation unpinned compId={}", compId);
    }

    @Override
    public void pinCompilationOnPage(Long compId) {
        Compilation compilation = getCompilationById(compId);
        compilation.setPinned(true);
        repository.save(compilation);
        logger.info("Compilation pinned compId={}", compId);
    }

    private Compilation getCompilationById(Long compId) {
        return repository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found by campId=" + compId));
    }
}
