package ru.practicum.ewm.categories.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
public class CategoryDto {

    @NotNull(message = "id should not be null")
    private Long id;
    @NotBlank(message = "name should not be blank")
    private String name;
}
