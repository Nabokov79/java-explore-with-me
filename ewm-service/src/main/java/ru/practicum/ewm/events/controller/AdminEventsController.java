package ru.practicum.ewm.events.controller;

import ru.practicum.ewm.events.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.paramRequest.ParamAdminRequest;
import ru.practicum.ewm.paramRequest.Param;
import ru.practicum.ewm.events.service.AdminEventsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@Validated
public class AdminEventsController {

    private final AdminEventsService service;

    @Autowired
    public AdminEventsController(AdminEventsService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<EventFullDto>> searchEvents(@RequestParam String users,
                                                           @RequestParam String states,
                                                           @RequestParam String categories,
                                                           @RequestParam String rangeStart,
                                                           @RequestParam String rangeEnd,
                                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                           @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        ParamAdminRequest param = Param.toParamAdminRequest(users, states, categories, rangeStart, rangeEnd);
        return ResponseEntity.ok().body(service.searchEvents(param, from, size));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventFullDto> editEvent(@PathVariable Long eventId,
                                                             @RequestBody AdminUpdateEventRequest adminUpdateEvent) {
        return ResponseEntity.ok().body(service.editEvent(eventId, adminUpdateEvent));
    }

    @PatchMapping("/{eventId}/publish")
    public ResponseEntity<EventFullDto> publishEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok().body(service.publishEvent(eventId));
    }

    @PatchMapping("{eventId}/reject")
    public ResponseEntity<EventFullDto> rejectEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok().body(service.rejectEvent(eventId));
    }
}
