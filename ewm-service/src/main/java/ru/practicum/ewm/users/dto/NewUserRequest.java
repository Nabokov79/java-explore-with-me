package ru.practicum.ewm.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
public class NewUserRequest {

    @NotBlank(message = "name should not be blank")
    private String name;

    @Email(message = "email is not correct")
    @NotNull(message = "email is null")
    private String email;
}
