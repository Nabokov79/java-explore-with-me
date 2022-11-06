package ru.practicum.ewm.requests.controller;

import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.requests.service.PrivateRequestsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestsController {

    private final PrivateRequestsService service;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok().body(service.getByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> add(@PathVariable Long userId, @RequestParam Long eventId) {
        return ResponseEntity.ok().body(service.add(userId, eventId));
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancel(@PathVariable Long userId, @PathVariable Long requestId) {
        return ResponseEntity.ok().body(service.cancel(userId, requestId));
    }
}
