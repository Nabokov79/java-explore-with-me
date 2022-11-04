package ru.practicum.ewm.categories.controller;

import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.NewCategoryDto;
import ru.practicum.ewm.categories.service.AdminCategoriesService;
import ru.practicum.ewm.common.Create;
import ru.practicum.ewm.common.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/admin/categories")
@Validated
public class AdminCategoriesController {

    private final AdminCategoriesService service;

    @Autowired
    public AdminCategoriesController(AdminCategoriesService service) {
        this.service = service;
    }

    @PatchMapping
    public ResponseEntity<CategoryDto> updateCategory(@Validated({Update.class})
                                                      @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok().body(service.updateCategory(categoryDto));
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Validated({Create.class})
                                                      @RequestBody NewCategoryDto categoryDto) {
        return ResponseEntity.ok().body(service.createCategory(categoryDto));
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Object> deleteCategory(@PathVariable Long catId) {
        service.deleteCategory(catId);
        return ResponseEntity.ok().body("Категория удалена");
    }
}
