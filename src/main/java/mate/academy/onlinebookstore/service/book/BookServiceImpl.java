package mate.academy.onlinebookstore.service.book;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.book.BookDto;
import mate.academy.onlinebookstore.dto.book.BookSearchParameters;
import mate.academy.onlinebookstore.dto.book.BookWithoutCategoryIdDto;
import mate.academy.onlinebookstore.dto.book.CreateBookRequestDto;
import mate.academy.onlinebookstore.exception.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.BookMapper;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.repository.SpecificationBuilder;
import mate.academy.onlinebookstore.repository.book.BookRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final SpecificationBuilder<Book> bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        return bookMapper.toBookDto(bookRepository.save(bookMapper.toBookEntity(requestDto)));
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toBookDto)
                .toList();
    }

    @Override
    public BookDto findBookById(Long id) {
        Book book = bookRepository.findBookById(id).orElseThrow(()
                -> new EntityNotFoundException("Can't find book by id: " + id)
        );
        return bookMapper.toBookDto(book);
    }

    @Override
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto update(Long id, CreateBookRequestDto requestDto) {
        Book updatedBook = bookRepository.findBookById(id)
                .map(book -> {
                    book.setTitle(requestDto.getTitle());
                    book.setAuthor(requestDto.getAuthor());
                    book.setIsbn(requestDto.getIsbn());
                    book.setDescription(requestDto.getDescription());
                    book.setPrice(requestDto.getPrice());
                    book.setCoverImage(requestDto.getCoverImage());
                    book.setCategories(bookMapper.toBookEntity(requestDto).getCategories());
                    return bookRepository.save(book); })
                .orElseThrow(() -> new EntityNotFoundException("Can't update book by id: " + id));
        return bookMapper.toBookDto(updatedBook);
    }

    @Override
    public List<BookDto> search(BookSearchParameters searchParameters, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParameters);
        return bookRepository.findAll(bookSpecification, pageable).stream()
                .map(bookMapper::toBookDto)
                .toList();
    }

    @Override
    public List<BookWithoutCategoryIdDto> findAllByCategory(Long id, Pageable pageable) {
        return bookRepository.findAllByCategoryId(id, pageable).stream()
                .map(bookMapper::toBookWithoutCategoryIdDto)
                .toList();
    }
}
