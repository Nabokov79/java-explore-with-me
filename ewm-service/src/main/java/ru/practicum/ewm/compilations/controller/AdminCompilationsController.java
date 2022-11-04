package ru.practicum.ewm.compilations.controller;

import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.compilations.service.AdminCompilationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/admin/compilations")
@Validated
public class AdminCompilationsController {

    private final AdminCompilationsService service;

    @Autowired
    public AdminCompilationsController(AdminCompilationsService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CompilationDto> addCompilation(@RequestBody NewCompilationDto compilation) {
        return ResponseEntity.ok().body(service.addCompilation(compilation));
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Object> deleteCompilationById(@PathVariable Long compId) {
        service.deleteCompilationById(compId);
        return ResponseEntity.ok().body("Подборка удалена");
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public ResponseEntity<Object> deleteEventFromCompilation(@PathVariable Long compId,
                                                           @PathVariable Long eventId) {
        service.deleteEventFromCompilation(compId, eventId);
        return ResponseEntity.ok().body("Событие удалено из подборки");
    }

    @PatchMapping("{compId}/events/{eventId}")
    public ResponseEntity<Object> addEventToCompilation(@PathVariable Long compId,
                                                      @PathVariable Long eventId) {
        service.addEventToCompilation(compId, eventId);
        return ResponseEntity.ok().body("Событие добавлено");
    }

    @DeleteMapping("/{compId}/pin")
    public ResponseEntity<Object> unpinCompilationOnPage(@PathVariable Long compId) {
        service.unpinCompilationOnPage(compId);
        return ResponseEntity.ok().body("Подборка откреплена");
    }

    @PatchMapping("/{compId}/pin")
    public ResponseEntity<Object> pinEventToCompilationPage(@PathVariable Long compId) {
        service.pinCompilationOnPage(compId);
        return ResponseEntity.ok().body("Подборка закреплена");
    }
}
