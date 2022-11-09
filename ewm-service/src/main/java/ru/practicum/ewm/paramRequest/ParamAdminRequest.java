package ru.practicum.ewm.paramRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.events.model.State;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ParamAdminRequest {

    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Size(min = 1)
    private List<Long> users;
    private List<State> states;
    @Size(min = 1)
    private List<Long> categories;
    @DateTimeFormat(pattern = DATETIME_PATTERN)
    private LocalDateTime rangeStart;
    @DateTimeFormat(pattern = DATETIME_PATTERN)
    private LocalDateTime rangeEnd;
}
