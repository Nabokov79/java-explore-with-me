package ru.practicum.ewm.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.categories.mapper.CategoriesMapper;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoriesRepository;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.NewCategoryDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventsRepository;
import ru.practicum.ewm.exeption.BadRequestException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCategoriesServiceImpl implements AdminCategoriesService {

    private final CategoriesRepository repository;
    private final PublicCategoriesService publicCategoriesService;
    private final EventsRepository eventsRepository;

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        publicCategoriesService.getById(categoryDto.getId());
        Category category = repository.save(CategoriesMapper.toCategory(categoryDto));
        log.info("Update category={}, categoryDto={}", category, categoryDto);
        return CategoriesMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto create(NewCategoryDto categoryDto) {
        Category category = repository.save(CategoriesMapper.toCategory(categoryDto));
        log.info("Save new category={}", category);
        return CategoriesMapper.toCategoryDto(category);
    }

    @Override
    public void delete(Long catId) {
        publicCategoriesService.getById(catId);
        Optional<Event> event = eventsRepository.findByCategoryId(catId);
        if (event.isEmpty()) {
            repository.deleteById(catId);
            log.info("Delete category with catId={}", catId);
        } else {
            throw new BadRequestException(String.format("Category with id=%s cannot be deleted.", catId));
        }

    }
}
