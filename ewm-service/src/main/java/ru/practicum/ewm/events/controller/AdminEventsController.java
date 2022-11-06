package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.common.Create;
import ru.practicum.ewm.events.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.paramRequest.ParamAdminRequest;
import ru.practicum.ewm.paramRequest.Param;
import ru.practicum.ewm.events.service.AdminEventsService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@Validated
@RequiredArgsConstructor
public class AdminEventsController {

    private final AdminEventsService service;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> search(@RequestParam String users,
                                                           @RequestParam String states,
                                                           @RequestParam String categories,
                                                           @RequestParam String rangeStart,
                                                           @RequestParam String rangeEnd,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        ParamAdminRequest param = Param.toParamAdminRequest(users, states, categories, rangeStart, rangeEnd);
        return ResponseEntity.ok().body(service.search(param, from, size));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventFullDto> edit(@PathVariable Long eventId,
                                      @Validated({Create.class})@RequestBody AdminUpdateEventRequest adminUpdateEvent) {
        return ResponseEntity.ok().body(service.edit(eventId, adminUpdateEvent));
    }

    @PatchMapping("/{eventId}/publish")
    public ResponseEntity<EventFullDto> publish(@PathVariable Long eventId) {
        return ResponseEntity.ok().body(service.publish(eventId));
    }

    @PatchMapping("{eventId}/reject")
    public ResponseEntity<EventFullDto> reject(@PathVariable Long eventId) {
        return ResponseEntity.ok().body(service.reject(eventId));
    }
}
