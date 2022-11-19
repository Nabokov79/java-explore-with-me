package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.events.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.model.State;
import ru.practicum.ewm.paramRequest.ParamAdminRequest;
import ru.practicum.ewm.paramRequest.Param;
import ru.practicum.ewm.events.service.AdminEventsService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public ResponseEntity<List<EventFullDto>> search(@RequestParam(required = false) List<Long> users,
                                                     @RequestParam(required = false) List<State> states,
                                                     @RequestParam(required = false) List<Long> categories,
                            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0", required = false) int from,
                            @Positive @RequestParam(name = "size", defaultValue = "10", required = false) int size,
           @RequestParam(required = false) String rangeStart,
           @RequestParam(required = false) String rangeEnd,
                                                     HttpServletRequest request) {
        ParamAdminRequest param = Param.toParamAdminRequest(users, states, categories, rangeStart, rangeEnd);
        return ResponseEntity.ok().body(service.search(param, request, from, size));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventFullDto> edit(@PathVariable Long eventId,
                                      @Validated@RequestBody AdminUpdateEventRequest adminUpdateEvent,
                                             HttpServletRequest request) {
        return ResponseEntity.ok().body(service.edit(eventId, adminUpdateEvent, request));
    }

    @PatchMapping("/{eventId}/publish")
    public ResponseEntity<EventFullDto> publish(@PathVariable Long eventId, HttpServletRequest request) {
        return ResponseEntity.ok().body(service.publish(eventId, request));
    }

    @PatchMapping("{eventId}/reject")
    public ResponseEntity<EventFullDto> reject(@PathVariable Long eventId, HttpServletRequest request) {
        return ResponseEntity.ok().body(service.reject(eventId, request));
    }
}
