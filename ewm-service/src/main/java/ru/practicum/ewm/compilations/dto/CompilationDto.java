package ru.practicum.ewm.compilations.dto;

import ru.practicum.ewm.events.dto.EventShortDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class CompilationDto {

    private Long id;
    private List<EventShortDto> events;
    private Boolean pinned;
    private String title;
}
