package ru.practicum.ewm.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.common.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
public class NewUserRequest {

    @NotBlank(groups = {Create.class}, message = "name should not be blank")
    private String name;

    @Email(groups = {Create.class}, message = "email is not correct")
    @NotNull(groups = {Create.class}, message = "email is null")
    private String email;
}
