package mate.academy.onlinebookstore.dto.book;

import java.math.BigDecimal;

public record BookWithoutCategoryIdDto(
        Long id,
        String title,
        String author,
        String isbn,
        BigDecimal price,
        String description,
        String coverImage
) {
}
