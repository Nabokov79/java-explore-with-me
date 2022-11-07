package ru.practicum.ewm.categories.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.common.Update;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
public class CategoryDto {

    @NotNull(groups = {Update.class})
    private Long id;
    @NotBlank(groups = {Update.class}, message = "name should not be blank")
    private String name;
}
