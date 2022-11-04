package ru.practicum.ewm.compilations.controller;

import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.service.PublicCompilationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.*;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@Validated
public class PublicCompilationsController {

    private final PublicCompilationsService service;

    @Autowired
    public PublicCompilationsController(PublicCompilationsService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getAllCompilations(
                                              @RequestParam(name = "pinned", defaultValue = "true") Boolean pinned,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        List<CompilationDto> compilationDtoList = service.getAllCompilations(pinned, from, size);
        return ResponseEntity.ok().body(compilationDtoList);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilationById(@PathVariable Long compId) {
        return ResponseEntity.ok().body(service.getCompilationById(compId));
    }
}
