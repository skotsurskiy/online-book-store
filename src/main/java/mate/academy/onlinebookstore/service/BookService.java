package mate.academy.onlinebookstore.service;

import java.util.List;
import mate.academy.onlinebookstore.dto.BookDto;
import mate.academy.onlinebookstore.dto.BookSearchParameters;
import mate.academy.onlinebookstore.dto.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto findBookById(Long id);

    void deleteBookById(Long id);

    BookDto update(Long id, CreateBookRequestDto requestDto);

    List<BookDto> search(BookSearchParameters searchParameters);
}
