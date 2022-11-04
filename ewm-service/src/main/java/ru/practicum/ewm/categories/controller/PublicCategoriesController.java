package ru.practicum.ewm.categories.controller;

import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.service.PublicCategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@Validated
public class PublicCategoriesController {

    private final PublicCategoriesService service;

    @Autowired
    public PublicCategoriesController(PublicCategoriesService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories(
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok().body(service.getAllCategories(from, size));
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long catId) {
        return ResponseEntity.ok().body(service.getCategoryById(catId));
    }
}