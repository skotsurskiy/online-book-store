package mate.academy.onlinebookstore.service.category;

import java.util.List;
import mate.academy.onlinebookstore.dto.category.CategoryDto;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto findById(Long id);

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto, Long id);

    void delete(Long id);
}
