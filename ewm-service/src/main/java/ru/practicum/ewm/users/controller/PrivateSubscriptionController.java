package ru.practicum.ewm.users.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.service.PrivateSubscriptionService;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/subscriptions/users/{userId}")
public class PrivateSubscriptionController {

    private final PrivateSubscriptionService service;

    @PostMapping("/subscriber/{subscriberId}/subscribe")
    public ResponseEntity<List<UserDto>> subscribe(@PathVariable Long userId, @PathVariable Long subscriberId) {
        return ResponseEntity.ok().body(service.subscribe(userId, subscriberId));
    }

    @PatchMapping("/subscriber/{subscriberId}/unsubscribe")
    public ResponseEntity<String> unsubscribe(@PathVariable Long userId, @PathVariable Long subscriberId) {
        service.unsubscribe(userId, subscriberId);
        return ResponseEntity.ok("Подписка отменена");
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> get(@PathVariable Long userId) {
        return ResponseEntity.ok().body(service.get(userId));
    }

    @GetMapping("/subscriber/{subscriberId}/event")
    public ResponseEntity<List<EventFullDto>> getEvents(@PathVariable Long userId, @PathVariable Long subscriberId,
                            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0", required = false) int from,
                            @Positive @RequestParam(name = "size", defaultValue = "10", required = false) int size) {
        return ResponseEntity.ok().body(service.getEvents(userId, subscriberId, from, size));
    }

    @DeleteMapping("/subscriber/{subscriberId}/delete")
    public ResponseEntity<String> delete(@PathVariable Long userId, @PathVariable Long subscriberId) {
        service.delete(userId, subscriberId);
        return ResponseEntity.ok("Подписка удалена.");
    }
}
