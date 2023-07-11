package ru.practicum.ewm.main.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.category.dto.NewCategoryDto;
import ru.practicum.ewm.main.category.mapper.CategoryMapper;
import ru.practicum.ewm.main.category.model.Category;
import ru.practicum.ewm.main.category.storage.CategoryStorage;
import ru.practicum.ewm.main.exception.NoSuchEntityException;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryStorage categoryStorage;

    @Override
    @Transactional
    public CategoryDto getCategoryById(long catId) {
        return CategoryMapper.toCategoryDto(
                categoryStorage.findById(catId).orElseThrow(
                        () -> new NoSuchEntityException(String.format("No such category with id = %s", catId))
                )
        );
    }

    @Override
    @Transactional
    public List<CategoryDto> getCategoryList(int from, int size) {
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        return CategoryMapper.toListCategoryDto(categoryStorage.findAll(page).getContent());
    }

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto categoryDto) {
        return CategoryMapper.toCategoryDto(categoryStorage.save(CategoryMapper.toCategory(categoryDto)));
    }

    @Override
    @Transactional
    public void deleteCategory(long catId) {
        try {
            categoryStorage.deleteById(catId);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchEntityException(String.format("No such category with id = %s", catId));
        }
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(NewCategoryDto categoryDto, long catId) {
        Category category = categoryStorage.findById(catId).orElseThrow(
                () -> new NoSuchEntityException(String.format("No such category with id = %s", catId))
        );

        return CategoryMapper.toCategoryDto(categoryStorage.save(new Category(catId, categoryDto.getName())));
    }
}
