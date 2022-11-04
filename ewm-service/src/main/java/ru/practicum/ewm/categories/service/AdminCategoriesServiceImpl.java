package ru.practicum.ewm.categories.service;

import ru.practicum.ewm.categories.mapper.CategoriesMapper;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoriesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.NewCategoryDto;

@Service
public class AdminCategoriesServiceImpl implements AdminCategoriesService {

    private final CategoriesRepository repository;
    private final PublicCategoriesService publicCategoriesService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AdminCategoriesServiceImpl(CategoriesRepository repository,
                                      PublicCategoriesService publicCategoriesService) {
        this.repository = repository;
        this.publicCategoriesService = publicCategoriesService;
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        publicCategoriesService.getCategoryById(categoryDto.getId());
        Category category = repository.save(CategoriesMapper.toCategory(categoryDto));
        logger.info("Update category={}, categoryDto={}", category, categoryDto);
        return CategoriesMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto createCategory(NewCategoryDto categoryDto) {
        Category category = repository.save(CategoriesMapper.toCategory(categoryDto));
        logger.info("Save new category={}", category);
        return CategoriesMapper.toCategoryDto(category);
    }

    @Override
    public void deleteCategory(Long catId) {
        publicCategoriesService.getCategoryById(catId);
        repository.deleteById(catId);
        logger.info("Delete category with catId={}", catId);
    }
}
