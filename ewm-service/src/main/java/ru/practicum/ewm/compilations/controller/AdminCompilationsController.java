package ru.practicum.ewm.compilations.controller;

import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.common.Create;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.compilations.service.AdminCompilationsService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationsController {

    private final AdminCompilationsService service;

    @PostMapping
    public ResponseEntity<CompilationDto> add(@Validated({Create.class}) @RequestBody NewCompilationDto compilation) {
        return ResponseEntity.ok().body(service.add(compilation));
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<String> deleteById(@PathVariable Long compId) {
        service.deleteById(compId);
        return ResponseEntity.ok("Подборка удалена");
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long compId, @PathVariable Long eventId) {
        service.deleteEvent(compId, eventId);
        return ResponseEntity.ok("Событие удалено из подборки");
    }

    @PatchMapping("{compId}/events/{eventId}")
    public ResponseEntity<String> addEvent(@PathVariable Long compId, @PathVariable Long eventId) {
        service.addEvent(compId, eventId);
        return ResponseEntity.ok("Событие добавлено");
    }

    @DeleteMapping("/{compId}/pin")
    public ResponseEntity<String> unpin(@PathVariable Long compId) {
        service.unpin(compId);
        return ResponseEntity.ok("Подборка откреплена");
    }

    @PatchMapping("/{compId}/pin")
    public ResponseEntity<String> pin(@PathVariable Long compId) {
        service.pin(compId);
        return ResponseEntity.ok("Подборка закреплена");
    }
}
