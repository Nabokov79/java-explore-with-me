package ru.practicum.ewm.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.categories.mapper.CategoriesMapper;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoriesRepository;
import ru.practicum.ewm.exeption.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.categories.dto.CategoryDto;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicCategoriesServiceImpl implements PublicCategoriesService {

    private final CategoriesRepository repository;

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        Pageable pageable = PageRequest.of(from / size,size);
        log.info("Get all category  with from={}, size={}", from, size);
        return CategoriesMapper.toListDto(repository.findAll(pageable).getContent());
    }

    @Override
    public CategoryDto getById(Long catId) {
        Category category = repository.findById(catId)
                      .orElseThrow(() -> new NotFoundException(String.format("Category with id= %s not found", catId)));
        log.info("Get category  by id={}", catId);
        return CategoriesMapper.toCategoryDto(category);
    }
}
