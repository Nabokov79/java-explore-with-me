package ru.practicum.ewm.categories.service;

import ru.practicum.ewm.categories.mapper.CategoriesMapper;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoriesRepository;
import ru.practicum.ewm.exeption.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.categories.dto.CategoryDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicCategoriesServiceImpl implements PublicCategoriesService {

    private final CategoriesRepository repository;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public PublicCategoriesServiceImpl(CategoriesRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size,size);
        List<CategoryDto> categoryDtoList = repository.findAll(pageable).stream()
                                                                        .map(CategoriesMapper::toCategoryDto)
                                                                        .collect(Collectors.toList());
        if (categoryDtoList.isEmpty()) {
            throw new NotFoundException("Categories not found");
        }
        logger.info("Get all category  with from={}, size={}", from, size);
        return categoryDtoList;
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = repository.findById(catId)
                      .orElseThrow(() -> new NotFoundException(String.format("Category with id= %s not found", catId)));
        logger.info("Get category  by id={}", catId);
        return CategoriesMapper.toCategoryDto(category);
    }
}
