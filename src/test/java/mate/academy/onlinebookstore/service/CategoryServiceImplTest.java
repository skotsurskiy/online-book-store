package mate.academy.onlinebookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import mate.academy.onlinebookstore.dto.category.CategoryRequestDto;
import mate.academy.onlinebookstore.dto.category.CategoryResponseDto;
import mate.academy.onlinebookstore.mapper.CategoryMapper;
import mate.academy.onlinebookstore.model.Category;
import mate.academy.onlinebookstore.repository.category.CategoryRepository;
import mate.academy.onlinebookstore.service.category.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    public static final String FIRST_CATEGORY_TITLE = "firstCategory";
    public static final String SECOND_CATEGORY_TITLE = "secondCategory";
    public static final long FIRST_VALID_ID = 1L;
    public static final long SECOND_VALID_ID = 2L;
    public static final String DESCRIPTION = "description";
    public static final long INVALID_ID = -1L;
    public static final String CATEGORY_NOT_FOUND_ERROR_MESSAGE = "Can't find category by id: -1";
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @Test
    @DisplayName("""
            using find all method with two categories, return list of two categories
            """)
    void findAll_WithTwoCategories_ReturnListOfTwoCategories() {
        Category firstCategory = createCategory(FIRST_VALID_ID, FIRST_CATEGORY_TITLE);
        Category secondCategory = createCategory(SECOND_VALID_ID, SECOND_CATEGORY_TITLE);

        List<Category> categories = List.of(firstCategory, secondCategory);

        CategoryResponseDto firstCategoryResponseDto = new CategoryResponseDto(
                FIRST_VALID_ID,
                FIRST_CATEGORY_TITLE,
                DESCRIPTION
        );
        CategoryResponseDto secondCategoryResponseDto = new CategoryResponseDto(
                SECOND_VALID_ID,
                SECOND_CATEGORY_TITLE,
                DESCRIPTION
        );

        when(categoryRepository.findAll(Pageable.unpaged()))
                .thenReturn(new PageImpl<>(categories));
        when(categoryMapper.toCategoryResponseDto(firstCategory))
                .thenReturn(firstCategoryResponseDto);
        when(categoryMapper.toCategoryResponseDto(secondCategory))
                .thenReturn(secondCategoryResponseDto);

        List<CategoryResponseDto> actual = categoryService.findAll(Pageable.unpaged());

        assertEquals(categories.size(), actual.size());
        assertThat(actual.get(0))
                .usingRecursiveComparison()
                .isEqualTo(firstCategoryResponseDto);
        assertThat(actual.get(1))
                .usingRecursiveComparison()
                .isEqualTo(secondCategoryResponseDto);
    }

    @Test
    @DisplayName("""
            using find by id method with correct id, return one CategoryResponseDto
            """)
    void findById_WithCorrectId_ReturnOneCategoryResponseDto() {
        Category category = createCategory(FIRST_VALID_ID, FIRST_CATEGORY_TITLE);
        CategoryResponseDto responseDto = new CategoryResponseDto(
                FIRST_VALID_ID,
                FIRST_CATEGORY_TITLE,
                DESCRIPTION
        );

        when(categoryRepository.findById(FIRST_VALID_ID)).thenReturn(Optional.of(category));
        when(categoryMapper.toCategoryResponseDto(category)).thenReturn(responseDto);

        CategoryResponseDto actual = categoryService.findById(FIRST_VALID_ID);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(responseDto);
    }

    @Test
    @DisplayName("""
            using find by id method with invalid id, throw EntityNotFoundException
            """)
    void findById_WithInvalidId_ThrowEntityNotFoundException() {
        when(categoryRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.findById(INVALID_ID)
        );

        assertEquals(CATEGORY_NOT_FOUND_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("""
            using update method with valid id and categoryRequestDto, return CategoryResponseDto
            """)
    void update_WithValidId_ReturnCategoryResponseDto() {
        Category initialCategory = createCategory(FIRST_VALID_ID, FIRST_CATEGORY_TITLE);
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto(
                SECOND_CATEGORY_TITLE,
                DESCRIPTION
        );
        CategoryResponseDto expected = new CategoryResponseDto(
                FIRST_VALID_ID,
                SECOND_CATEGORY_TITLE,
                DESCRIPTION
        );

        when(categoryRepository.findById(FIRST_VALID_ID)).thenReturn(Optional.of(initialCategory));
        doNothing().when(categoryMapper).updateCategoryFromDto(categoryRequestDto, initialCategory);
        when(categoryRepository.save(initialCategory)).thenReturn(initialCategory);
        when(categoryMapper.toCategoryResponseDto(initialCategory)).thenReturn(expected);

        CategoryResponseDto actual = categoryService.update(categoryRequestDto, FIRST_VALID_ID);

        assertEquals(expected.name(), actual.name());
    }

    @Test
    @DisplayName("""
            using update method with invalid id, throw EntityNotFoundException
            """)
    void update_WithInvalidId_ThrowEntityNotFoundException() {
        CategoryRequestDto requestDto = new CategoryRequestDto(
                FIRST_CATEGORY_TITLE,
                DESCRIPTION
        );

        when(categoryRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.update(requestDto, INVALID_ID)
        );

        assertEquals(CATEGORY_NOT_FOUND_ERROR_MESSAGE, exception.getMessage());
    }

    private Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setDescription(DESCRIPTION);
        return category;
    }
}
