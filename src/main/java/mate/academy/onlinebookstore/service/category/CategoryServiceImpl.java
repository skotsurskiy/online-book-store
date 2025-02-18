package mate.academy.onlinebookstore.service.category;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.category.CategoryDto;
import mate.academy.onlinebookstore.exception.EntityNotFoundException;
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
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto findById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Can't find category by id: " + id));
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        return categoryMapper.toCategoryDto(categoryRepository
                .save(categoryMapper.toCategoryEntity(categoryDto)));
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto, Long id) {
        Category categoryEntity = categoryMapper.toCategoryEntity(categoryDto);
        categoryEntity.setId(id);
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryEntity));
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
