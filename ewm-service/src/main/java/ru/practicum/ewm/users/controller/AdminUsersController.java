package ru.practicum.ewm.users.controller;

import ru.practicum.ewm.users.dto.NewUserRequest;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.service.AdminUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@Validated
public class AdminUsersController {

    private final AdminUsersService service;

    @Autowired
    public AdminUsersController(AdminUsersService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(@RequestParam String ids,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok().body(service.getAllUsers(ids, from, size));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody NewUserRequest newUser) {
        return ResponseEntity.ok().body(service.createUser(newUser));
    }

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        service.deleteUser(userId);
        return ResponseEntity.ok().body("Пользователь удален");
    }
}
