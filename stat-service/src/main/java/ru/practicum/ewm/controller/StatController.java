package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.service.StatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatController {

    private final StatService service;

    @PostMapping("/hit")
    public void saveStat(@RequestBody EndpointHit endpointHit) {
        service.saveStat(endpointHit);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStats>> getAllStats(
                                               @RequestParam(value = "start") String start,
                                               @RequestParam(value = "end") String end,
                                               @RequestParam(value = "uris") List<String> uris,
                                               @RequestParam(value = "unique", defaultValue = "false") Boolean unique) {
        return ResponseEntity.ok().body(service.getAllStats(start, end, uris, unique));
    }
}
