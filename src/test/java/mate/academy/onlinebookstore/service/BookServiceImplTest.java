package mate.academy.onlinebookstore.service;

import static mate.academy.onlinebookstore.util.TestUtil.AUTHOR;
import static mate.academy.onlinebookstore.util.TestUtil.BOOK_NOT_FOUND_ERROR_MESSAGE;
import static mate.academy.onlinebookstore.util.TestUtil.COVER_IMAGE;
import static mate.academy.onlinebookstore.util.TestUtil.DESCRIPTION;
import static mate.academy.onlinebookstore.util.TestUtil.EXPECTED_LIST_SIZE;
import static mate.academy.onlinebookstore.util.TestUtil.FIRST_BOOK_TITLE;
import static mate.academy.onlinebookstore.util.TestUtil.FIRST_LIST_INDEX;
import static mate.academy.onlinebookstore.util.TestUtil.FIRST_VALID_ID;
import static mate.academy.onlinebookstore.util.TestUtil.INVALID_ID;
import static mate.academy.onlinebookstore.util.TestUtil.ISBN;
import static mate.academy.onlinebookstore.util.TestUtil.PRICE;
import static mate.academy.onlinebookstore.util.TestUtil.SECOND_BOOK_TITLE;
import static mate.academy.onlinebookstore.util.TestUtil.SECOND_LIST_INDEX;
import static mate.academy.onlinebookstore.util.TestUtil.SECOND_VALID_ID;
import static mate.academy.onlinebookstore.util.TestUtil.UPDATED_BOOK_TITLE;
import static mate.academy.onlinebookstore.util.TestUtil.createBook;
import static mate.academy.onlinebookstore.util.TestUtil.createBookDto;
import static mate.academy.onlinebookstore.util.TestUtil.createBookRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import mate.academy.onlinebookstore.dto.book.BookDto;
import mate.academy.onlinebookstore.dto.book.BookSearchParameters;
import mate.academy.onlinebookstore.dto.book.BookWithoutCategoryIdDto;
import mate.academy.onlinebookstore.dto.book.CreateBookRequestDto;
import mate.academy.onlinebookstore.mapper.BookMapper;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.repository.SpecificationBuilder;
import mate.academy.onlinebookstore.repository.book.BookRepository;
import mate.academy.onlinebookstore.service.book.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private SpecificationBuilder<Book> specificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("""
            using findAll method, return two bookDtos
            """)
    void findAll_WithTwoBooks_ReturnTwoBooks() {
        Book firstBook = createBook(FIRST_VALID_ID, FIRST_BOOK_TITLE);
        BookDto firstBookDto = createBookDto(FIRST_VALID_ID, FIRST_BOOK_TITLE);
        Book secondBook = createBook(SECOND_VALID_ID, SECOND_BOOK_TITLE);
        BookDto secondBookDto = createBookDto(SECOND_VALID_ID, SECOND_BOOK_TITLE);

        List<Book> books = List.of(firstBook, secondBook);

        when(bookRepository.findAll(Pageable.unpaged())).thenReturn(new PageImpl<>(books));
        when(bookMapper.toBookDto(firstBook)).thenReturn(firstBookDto);
        when(bookMapper.toBookDto(secondBook)).thenReturn(secondBookDto);

        List<BookDto> actual = bookService.findAll(Pageable.unpaged());

        assertEquals(EXPECTED_LIST_SIZE, actual.size());
        assertEquals(firstBookDto, actual.get(FIRST_LIST_INDEX));
        assertEquals(secondBookDto, actual.get(SECOND_LIST_INDEX));

        verify(bookRepository).findAll(Pageable.unpaged());
        verify(bookMapper).toBookDto(firstBook);
        verify(bookMapper).toBookDto(secondBook);
    }

    @Test
    @DisplayName("""
            find book by id with valid id, return bookDto
            """)
    void findById_WithValidBookId_ReturnOneBookDto() {
        Book book = createBook(FIRST_VALID_ID, FIRST_BOOK_TITLE);
        BookDto bookDto = createBookDto(FIRST_VALID_ID, FIRST_BOOK_TITLE);

        when(bookRepository.findBookById(FIRST_VALID_ID)).thenReturn(Optional.of(book));
        when(bookMapper.toBookDto(book)).thenReturn(bookDto);

        BookDto actual = bookService.findBookById(FIRST_VALID_ID);

        assertEquals(bookDto, actual);

        verify(bookRepository).findBookById(FIRST_VALID_ID);
        verify(bookMapper).toBookDto(book);
    }

    @Test
    @DisplayName("""
            find book by id with not valid id, throw EntityNotFoundException
            """)
    void findById_WithNonExistingBookId_ThrowEntityNotFoundException() {
        when(bookRepository.findBookById(INVALID_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.findBookById(INVALID_ID)
        );

        assertEquals(BOOK_NOT_FOUND_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("""
            update book by valid id and requestDto, return bookDto
            """)
    void update_WithValidIdAndRequestDto_returnOneBookDto() {
        Book initialBook = createBook(FIRST_VALID_ID, FIRST_BOOK_TITLE);
        CreateBookRequestDto requestDto = createBookRequestDto(UPDATED_BOOK_TITLE);
        BookDto updatedBook = createBookDto(FIRST_VALID_ID, UPDATED_BOOK_TITLE);

        when(bookRepository.findBookById(FIRST_VALID_ID)).thenReturn(Optional.of(initialBook));
        Mockito.doNothing().when(bookMapper).updateBookFromDto(requestDto, initialBook);
        when(bookRepository.save(initialBook)).thenReturn(initialBook);
        when(bookMapper.toBookDto(initialBook)).thenReturn(updatedBook);

        BookDto actual = bookService.update(FIRST_VALID_ID, requestDto);

        assertEquals(updatedBook, actual);

        verify(bookRepository).findBookById(FIRST_VALID_ID);
        verify(bookMapper).updateBookFromDto(requestDto, initialBook);
        verify(bookRepository).save(initialBook);
        verify(bookMapper).toBookDto(initialBook);
    }

    @Test
    @DisplayName("""
            update book by invalid id, throw EntityNotFoundException
            """)
    void update_WithInvalidId_ThrowEntityNotFoundException() {
        CreateBookRequestDto requestDto = createBookRequestDto(UPDATED_BOOK_TITLE);

        when(bookRepository.findBookById(INVALID_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.update(INVALID_ID, requestDto)
        );

        assertEquals(BOOK_NOT_FOUND_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("""
            search book by parameters, return list of one bookDto
            """)
    void search_WithSearchParameters_ReturnOneBookDto() {
        BookSearchParameters searchParameters = new BookSearchParameters(
                new String[]{FIRST_BOOK_TITLE},
                new String[]{AUTHOR},
                new BigDecimal[]{PRICE},
                new Long[]{FIRST_VALID_ID}
        );
        Specification<Book> bookSpecification = specificationBuilder.build(searchParameters);
        Book book = createBook(FIRST_VALID_ID, FIRST_BOOK_TITLE);
        BookDto bookDto = createBookDto(FIRST_VALID_ID, FIRST_BOOK_TITLE);

        when(bookRepository.findAll(bookSpecification, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(book)));
        when(bookMapper.toBookDto(book)).thenReturn(bookDto);

        List<BookDto> actual = bookService.search(searchParameters, Pageable.unpaged());

        assertEquals(SECOND_LIST_INDEX, actual.size());
        assertEquals(bookDto, actual.get(FIRST_LIST_INDEX));

        verify(bookRepository).findAll(bookSpecification, Pageable.unpaged());
        verify(bookMapper).toBookDto(book);
    }

    @Test
    @DisplayName("""
            find all by valid category id, return list of one BookWithoutCategoryIdDto
            """)
    void findAllByCategory_WithValidCategoryId_ReturnBookWithoutCategoryIdDto() {
        Book book = createBook(FIRST_VALID_ID, FIRST_BOOK_TITLE);
        BookWithoutCategoryIdDto bookWithoutCategoryIdDto = new BookWithoutCategoryIdDto(
                FIRST_VALID_ID,
                FIRST_BOOK_TITLE,
                AUTHOR,
                ISBN,
                PRICE,
                DESCRIPTION,
                COVER_IMAGE
        );

        when(bookRepository.findAllByCategoryId(FIRST_VALID_ID, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(book)));
        when(bookMapper.toBookWithoutCategoryIdDto(book))
                .thenReturn(bookWithoutCategoryIdDto);

        List<BookWithoutCategoryIdDto> actual
                = bookService.findAllByCategory(FIRST_VALID_ID, Pageable.unpaged());

        assertEquals(SECOND_LIST_INDEX, actual.size());
        assertEquals(bookWithoutCategoryIdDto, actual.get(FIRST_LIST_INDEX));

        verify(bookRepository).findAllByCategoryId(FIRST_VALID_ID, Pageable.unpaged());
        verify(bookMapper).toBookWithoutCategoryIdDto(book);
    }
}
