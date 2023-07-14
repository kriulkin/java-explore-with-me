package ru.practicum.ewm.main.category.service;

import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto getCategoryById(long catId);

    List<CategoryDto> getCategoryList(int from, int size);

    CategoryDto addCategory(NewCategoryDto categoryDto);

    void deleteCategory(long catId);

    CategoryDto updateCategory(NewCategoryDto categoryDto, long catId);
}
