package mate.academy.onlinebookstore.service.category;

import java.util.List;
import mate.academy.onlinebookstore.dto.category.CategoryRequestDto;
import mate.academy.onlinebookstore.dto.category.CategoryResponseDto;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    List<CategoryResponseDto> findAll(Pageable pageable);

    CategoryResponseDto findById(Long id);

    CategoryResponseDto save(CategoryRequestDto categoryDto);

    CategoryResponseDto update(CategoryRequestDto categoryDto, Long id);

    void delete(Long id);
}
