package ru.practicum.ewm.categories.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Setter
@Getter
@AllArgsConstructor
public class CategoryDto {

    @NotNull(message = "should not be null")
    @Positive(message = "should not be negative")
    private Long id;
    @NotBlank(message = "should not be blank")
    private String name;
}
