package ru.practicum.ewm.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@AllArgsConstructor
public class NewUserRequest {

    @NotBlank(message = "name should not be blank")
    private String name;
    @NotBlank(message = "email should not be blank")
    @Email(message = "email is not correct")
    private String email;
}
