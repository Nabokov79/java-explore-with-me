package ru.practicum.ewm.requests.controller;

import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.requests.service.PrivateRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
public class PrivateRequestsController {

    private final PrivateRequestsService service;

    @Autowired
    public PrivateRequestsController(PrivateRequestsService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getRequestByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok().body(service.getRequestByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> addRequest(@PathVariable Long userId,
                                                              @RequestParam Long eventId) {
        return ResponseEntity.ok().body(service.addRequest(userId, eventId));
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable Long userId,
                                                                 @PathVariable Long requestId) {
        return ResponseEntity.ok().body(service.cancelRequest(userId, requestId));
    }
}
