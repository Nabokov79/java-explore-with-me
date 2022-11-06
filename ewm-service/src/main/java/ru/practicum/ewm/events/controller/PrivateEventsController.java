package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.common.Create;
import ru.practicum.ewm.common.Update;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.UpdateEventRequest;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.events.service.PrivateEventsService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventsController {

    private final PrivateEventsService service;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getByUserId(@PathVariable Long userId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok().body(service.getByUserId(userId, from, size));
    }

    @PatchMapping
    public ResponseEntity<EventFullDto> changeByUserId(@PathVariable Long userId,
                                              @Validated({Update.class}) @RequestBody UpdateEventRequest eventRequest) {
        return ResponseEntity.ok().body(service.changeByUserId(userId, eventRequest));
    }

    @PostMapping
    public ResponseEntity<EventFullDto> create(@PathVariable Long userId,
                                                    @Validated({Create.class}) @RequestBody NewEventDto newEventDto) {
        return ResponseEntity.ok().body(service.create(userId, newEventDto));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getInfoCurrentUser(@PathVariable Long userId, @PathVariable Long eventId) {
        return ResponseEntity.ok().body(service.getInfoCurrentUser(userId,eventId));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> cancel(@PathVariable Long userId, @PathVariable Long eventId) {
        return ResponseEntity.ok().body(service.cancel(userId,eventId));
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequestInfoEventsCurrentUser(@PathVariable Long userId,
                                                                                         @PathVariable Long eventId) {
        List<ParticipationRequestDto> requestList = service.getInfoRequestEventsCurrentUser(userId, eventId);
        return ResponseEntity.ok().body(requestList);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public ResponseEntity<ParticipationRequestDto> confirmRequestUsers(@PathVariable Long userId,
                                                                       @PathVariable Long eventId,
                                                                       @PathVariable Long reqId) {
        return ResponseEntity.ok().body(service.confirmRequestUsers(userId, eventId, reqId));
    }

    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public ResponseEntity<ParticipationRequestDto> rejectRequestUsers(@PathVariable Long userId,
                                                                      @PathVariable Long eventId,
                                                                      @PathVariable Long reqId) {
        return ResponseEntity.ok().body(service.rejectRequestUsers(userId, eventId, reqId));
    }
}