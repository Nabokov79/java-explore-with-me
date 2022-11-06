package ru.practicum.ewm.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.common.Create;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class NewCompilationDto {

    private List<Long> events;
    private Boolean pinned;
    @NotBlank(groups = {Create.class}, message = "title compilation should not be blank")
    private String title;
}
