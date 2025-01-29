package mate.academy.onlinebookstore.service;

import java.util.List;
import mate.academy.onlinebookstore.dto.BookDto;
import mate.academy.onlinebookstore.dto.CreateBookRequestDto;
import mate.academy.onlinebookstore.exception.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.BookMapper;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        return bookMapper.toBookDto(bookRepository.save(bookMapper.toBookEntity(requestDto)));
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
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
                    book.setId(id);
                    book.setTitle(requestDto.getTitle());
                    book.setAuthor(requestDto.getAuthor());
                    book.setIsbn(requestDto.getIsbn());
                    book.setDescription(requestDto.getDescription());
                    book.setPrice(requestDto.getPrice());
                    book.setCoverImage(requestDto.getCoverImage());
                    return bookRepository.save(book); })
                .orElseThrow(() -> new EntityNotFoundException("Can't update book by id: " + id));
        return bookMapper.toBookDto(updatedBook);
    }
}
