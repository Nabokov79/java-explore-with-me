package ru.practicum.ewm.categories.service;

import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.NewCategoryDto;

public interface AdminCategoriesService {

    CategoryDto update(CategoryDto categoryDto);

    CategoryDto create(NewCategoryDto categoryDto);

    void delete(Long catId);
}