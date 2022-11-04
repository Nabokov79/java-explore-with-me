package ru.practicum.ewm.events.controller;

import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.paramRequest.ParamUserRequest;
import ru.practicum.ewm.paramRequest.Param;
import ru.practicum.ewm.events.service.PublicEventsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/events")

public class PublicEventsController {

    private final PublicEventsService service;

    @Autowired
    public PublicEventsController(PublicEventsService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAllEvents(@RequestParam String text,
                                                            @RequestParam String categories,
                                                            @RequestParam Boolean paid,
                                                            @RequestParam String rangeStart,
                                                            @RequestParam String rangeEnd,
                                                            @RequestParam Boolean onlyAvailable,
                                              @RequestParam(name = "sort", defaultValue = "EVENT_DATE") String sort,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") int size,
                                                            HttpServletRequest request) {
        ParamUserRequest paramUserRequest = Param.toParamUserRequest(text,categories, paid, rangeStart, rangeEnd,
                                                                                             onlyAvailable, sort
                                                                                             );
        List<EventShortDto> eventShortDtoList = service.getAllEvents(paramUserRequest, from, size, request);
        return ResponseEntity.ok().body(eventShortDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getFullInfoEvent(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok().body(service.getFullInfoEvent(id, request));
    }
}
