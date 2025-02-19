package mate.academy.onlinebookstore.mapper;

import mate.academy.onlinebookstore.config.MapperConfig;
import mate.academy.onlinebookstore.dto.category.CategoryRequestDto;
import mate.academy.onlinebookstore.dto.category.CategoryResponseDto;
import mate.academy.onlinebookstore.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryResponseDto toCategoryResponseDto(Category category);

    Category toCategoryEntity(CategoryRequestDto categoryDto);

    CategoryResponseDto toCategoryRequestDto(Category category);

    @Mapping(target = "id", ignore = true)
    void updateCategoryFromDto(CategoryRequestDto requestDto, @MappingTarget Category category);
}
