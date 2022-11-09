package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.paramRequest.ParamUserRequest;
import ru.practicum.ewm.paramRequest.Param;
import ru.practicum.ewm.events.service.PublicEventsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@Validated
@RequiredArgsConstructor
public class PublicEventsController {

    private final PublicEventsService service;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAll(@RequestParam(required = false) String text,
                                                      @RequestParam(required = false) List<Long> categories,
                                                      @RequestParam(required = false) Boolean paid,
                                                      @RequestParam(required = false) String rangeStart,
                                                      @RequestParam(required = false) String rangeEnd,
                                                      @RequestParam(required = false) Boolean onlyAvailable,
                                              @RequestParam(name = "sort", defaultValue = "EVENT_DATE") String sort,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") int size,
                                                            HttpServletRequest request) {
        ParamUserRequest paramUserRequest = Param.toParamUserRequest(text,categories, paid, rangeStart, rangeEnd,
                                                                                             onlyAvailable, sort
                                                                                             );
        List<EventShortDto> eventShortDtoList = service.getAll(paramUserRequest, from, size, request);
        return ResponseEntity.ok().body(eventShortDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getFullInfo(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok().body(service.getFullInfo(id, request));
    }
}
