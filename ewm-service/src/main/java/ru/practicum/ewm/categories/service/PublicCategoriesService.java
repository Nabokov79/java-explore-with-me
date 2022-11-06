package ru.practicum.ewm.categories.service;

import ru.practicum.ewm.categories.dto.CategoryDto;
import java.util.List;

public interface PublicCategoriesService {

    List<CategoryDto> getAll(int from, int size);

    CategoryDto getById(Long catId);
}