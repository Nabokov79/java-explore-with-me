package ru.practicum.ewm.categories.controller;

import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.NewCategoryDto;
import ru.practicum.ewm.categories.service.AdminCategoriesService;
import ru.practicum.ewm.common.Create;
import ru.practicum.ewm.common.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class AdminCategoriesController {

    private final AdminCategoriesService service;

    @PatchMapping
    public ResponseEntity<CategoryDto> update(@Validated({Update.class}) @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok().body(service.update(categoryDto));
    }

    @PostMapping
    public ResponseEntity<CategoryDto> create(@Validated({Create.class}) @RequestBody NewCategoryDto categoryDto) {
        return ResponseEntity.ok().body(service.create(categoryDto));
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<String> delete(@PathVariable Long catId) {
        service.delete(catId);
        return ResponseEntity.ok("Категория удалена");
    }
}
