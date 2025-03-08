package mate.academy.onlinebookstore.service.category;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.category.CategoryRequestDto;
import mate.academy.onlinebookstore.dto.category.CategoryResponseDto;
import mate.academy.onlinebookstore.exception.OrderProcessingException;
import mate.academy.onlinebookstore.mapper.CategoryMapper;
import mate.academy.onlinebookstore.model.Category;
import mate.academy.onlinebookstore.repository.category.CategoryRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponseDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toCategoryResponseDto)
                .toList();
    }

    @Override
    public CategoryResponseDto findById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(()
                -> new OrderProcessingException("Can't find category by id: " + id));
        return categoryMapper.toCategoryResponseDto(category);
    }

    @Override
    public CategoryResponseDto save(CategoryRequestDto categoryDto) {
        return categoryMapper.toCategoryRequestDto(categoryRepository
                .save(categoryMapper.toCategoryEntity(categoryDto)));
    }

    @Override
    public CategoryResponseDto update(CategoryRequestDto categoryDto, Long id) {
        Category updatedCategory = categoryRepository.findById(id).orElseThrow(()
                -> new OrderProcessingException("Can't update category by id: " + id));
        categoryMapper.updateCategoryFromDto(categoryDto, updatedCategory);
        return categoryMapper.toCategoryResponseDto(categoryRepository.save(updatedCategory));
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
