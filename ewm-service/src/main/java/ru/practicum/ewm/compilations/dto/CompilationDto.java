package ru.practicum.ewm.compilations.dto;

import lombok.NoArgsConstructor;
import ru.practicum.ewm.events.dto.EventFullDto;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class CompilationDto {

    private Long id;
    private List<EventFullDto> events;
    private Boolean pinned;
    private String title;
}
