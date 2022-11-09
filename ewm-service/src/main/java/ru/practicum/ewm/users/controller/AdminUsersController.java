package ru.practicum.ewm.users.controller;

import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.users.dto.NewUserRequest;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.service.AdminUsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@Validated
@RequiredArgsConstructor
public class AdminUsersController {

    private final AdminUsersService service;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll(@RequestParam List<Long> ids,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok().body(service.getAll(ids, from, size));
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@Validated @RequestBody NewUserRequest newUser) {
        return ResponseEntity.ok().body(service.create(newUser));
    }

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<String> delete(@PathVariable Long userId) {
        service.delete(userId);
        return ResponseEntity.ok("Пользователь удален");
    }
}
