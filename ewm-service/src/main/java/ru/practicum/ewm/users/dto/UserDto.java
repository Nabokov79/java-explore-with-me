package ru.practicum.ewm.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String name;
    private String email;
    private Boolean subscription;
}
