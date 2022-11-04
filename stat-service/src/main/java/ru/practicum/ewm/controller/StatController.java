package ru.practicum.ewm.controller;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StatController {

    private final StatService service;

    @Autowired
    public StatController(StatService service) {
        this.service = service;
    }

    @PostMapping("/hit")
    public ResponseEntity<String> saveStat(@RequestBody EndpointHitDto endpointHitList) {
        service.saveStat(endpointHitList);
        return ResponseEntity.ok().body("Информация сохранена");
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStats>> getAllStats(
                                               @RequestParam(value = "start") String start,
                                               @RequestParam(value = "end") String end,
                                               @RequestParam(value = "uris") String uris,
                                               @RequestParam(value = "unique", defaultValue = "false") Boolean unique) {
        return ResponseEntity.ok().body(service.getAllStats(start, end, uris, unique));
    }
}
