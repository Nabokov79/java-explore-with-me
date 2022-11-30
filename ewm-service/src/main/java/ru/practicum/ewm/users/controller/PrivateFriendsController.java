package ru.practicum.ewm.users.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.model.Friendship;
import ru.practicum.ewm.users.service.PrivateFriendsService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/friendship/users/{userId}")
public class PrivateFriendsController {

    private final PrivateFriendsService service;

    @PostMapping("/requester/{requesterId}")
    public ResponseEntity<String> request(@PathVariable Long userId, @PathVariable Long requesterId) {
        service.request(userId, requesterId);
        return ResponseEntity.ok("Заявка принята");
    }

    @GetMapping
    public ResponseEntity<List<Friendship>> getRequests(@PathVariable Long userId) {
        return ResponseEntity.ok().body(service.getRequests(userId));
    }

    @PatchMapping("/requester/{requesterId}/confirm")
    public ResponseEntity<String> confirm(@PathVariable Long userId, @PathVariable Long requesterId) {
        service.confirm(userId, requesterId);
        return ResponseEntity.ok("Заявка одобрена");

    }

    @PatchMapping("/requester/{requesterId}/reject")
    public ResponseEntity<String> reject(@PathVariable Long userId, @PathVariable Long requesterId) {
        service.reject(userId, requesterId);
        return ResponseEntity.ok("Заявка отклонена");
    }

    @GetMapping("/friends")
    public ResponseEntity<List<UserDto>> get(@PathVariable Long userId) {
        return ResponseEntity.ok().body(service.get(userId));
    }

    @DeleteMapping("/friend/{friendId}/delete")
    public ResponseEntity<String> delete(@PathVariable Long userId, @PathVariable Long friendId) {
        service.delete(userId, friendId);
        return ResponseEntity.ok("Пользователь удален.");
    }
}
