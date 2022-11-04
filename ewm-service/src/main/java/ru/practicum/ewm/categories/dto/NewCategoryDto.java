package ru.practicum.ewm.categories.dto;

import ru.practicum.ewm.common.Create;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewCategoryDto {

    @NotBlank(groups = {Create.class}, message = "name category should not be blank")
    private String name;
}
