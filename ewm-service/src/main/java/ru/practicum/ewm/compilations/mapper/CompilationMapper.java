package ru.practicum.ewm.compilations.mapper;

import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.events.mapper.EventMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = new Compilation();
        compilation.setPinned(compilationDto.getPinned());
        compilation.setTitle(compilationDto.getTitle());
        return compilation;
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(compilation.getId(),
                compilation.getEventsList().stream().map(EventMapper::toEventShortDto).collect(Collectors.toList()),
                compilation.getPinned(),
                compilation.getTitle());
    }
}
