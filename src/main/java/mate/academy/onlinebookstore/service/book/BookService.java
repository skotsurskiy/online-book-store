package mate.academy.onlinebookstore.service.book;

import java.util.List;
import mate.academy.onlinebookstore.dto.book.BookDto;
import mate.academy.onlinebookstore.dto.book.BookSearchParameters;
import mate.academy.onlinebookstore.dto.book.BookWithoutCategoryIdDto;
import mate.academy.onlinebookstore.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto findBookById(Long id);

    void deleteBookById(Long id);

    BookDto update(Long id, CreateBookRequestDto requestDto);

    List<BookDto> search(BookSearchParameters searchParameters, Pageable pageable);

    List<BookWithoutCategoryIdDto> findAllByCategory(Long id);
}
