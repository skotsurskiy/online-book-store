package mate.academy.onlinebookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.onlinebookstore.dto.book.BookDto;
import mate.academy.onlinebookstore.dto.book.BookSearchParameters;
import mate.academy.onlinebookstore.dto.book.BookWithoutCategoryIdDto;
import mate.academy.onlinebookstore.dto.book.CreateBookRequestDto;
import mate.academy.onlinebookstore.mapper.BookMapper;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.Category;
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
    public static final long FIRST_VALID_ID = 1L;
    public static final long SECOND_VALID_ID = 2L;
    public static final long INVALID_ID = -1L;
    public static final String FIRST_BOOK_TITLE = "firstBook";
    public static final String SECOND_BOOK_TITLE = "secondBook";
    public static final String UPDATED_BOOK_TITLE = "Updated title";
    public static final String AUTHOR = "Author";
    public static final String ISBN = "1111-2222-3333-4444";
    public static final BigDecimal PRICE = BigDecimal.valueOf(19.99);
    public static final String DESCRIPTION = "description";
    public static final String COVER_IMAGE = "CoverImage";
    public static final String CATEGORY = "Category";
    public static final int FIRST_LIST_INDEX = 0;
    public static final int SECOND_LIST_INDEX = 1;
    public static final int EXPECTED_LIST_SIZE = 2;
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
        assertEquals(firstBookDto.getTitle(), actual.get(FIRST_LIST_INDEX).getTitle());
        assertEquals(secondBookDto.getTitle(), actual.get(SECOND_LIST_INDEX).getTitle());
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

        assertEquals(bookDto.getTitle(), actual.getTitle());
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

        assertEquals("Can't find book by id: " + INVALID_ID, exception.getMessage());
    }

    @Test
    @DisplayName("""
            update book by valid id and requestDto, return bookDto
            """)
    void update_WithValidIdAndRequestDto_returnOneBookDto() {
        Book initialBook = createBook(FIRST_VALID_ID, FIRST_BOOK_TITLE);
        CreateBookRequestDto requestDto = createBookRequestDto();
        BookDto updatedBook = createBookDto(FIRST_VALID_ID, UPDATED_BOOK_TITLE);

        when(bookRepository.findBookById(FIRST_VALID_ID)).thenReturn(Optional.of(initialBook));
        Mockito.doNothing().when(bookMapper).updateBookFromDto(requestDto, initialBook);
        when(bookRepository.save(initialBook)).thenReturn(initialBook);
        when(bookMapper.toBookDto(initialBook)).thenReturn(updatedBook);

        BookDto actual = bookService.update(FIRST_VALID_ID, requestDto);

        assertEquals(updatedBook, actual);
    }

    @Test
    @DisplayName("""
            update book by invalid id, throw EntityNotFoundException
            """)
    void update_WithInvalidId_ThrowEntityNotFoundException() {
        CreateBookRequestDto requestDto = createBookRequestDto();

        when(bookRepository.findBookById(INVALID_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.update(INVALID_ID, requestDto)
        );

        assertEquals("Can't find book by id: " + INVALID_ID, exception.getMessage());
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
        assertEquals(FIRST_BOOK_TITLE, actual.get(FIRST_LIST_INDEX).getTitle());
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
        assertEquals(FIRST_BOOK_TITLE, actual.get(FIRST_LIST_INDEX).title());
    }

    private Book createBook(Long id, String title) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(AUTHOR);
        book.setPrice(PRICE);
        book.setIsbn(ISBN);
        book.setCategories(Set.of(createCategory(id)));
        return book;
    }

    private Category createCategory(Long id) {
        Category category = new Category();
        category.setId(id);
        category.setName(CATEGORY);
        return category;
    }

    private CreateBookRequestDto createBookRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(UPDATED_BOOK_TITLE);
        requestDto.setAuthor(AUTHOR);
        requestDto.setIsbn(ISBN);
        requestDto.setPrice(PRICE);
        requestDto.setCategoryIds(List.of(FIRST_VALID_ID));
        return requestDto;
    }

    private BookDto createBookDto(Long id, String title) {
        BookDto bookDto = new BookDto();
        bookDto.setId(id);
        bookDto.setTitle(title);
        bookDto.setAuthor(AUTHOR);
        bookDto.setPrice(PRICE);
        bookDto.setIsbn(ISBN);
        bookDto.setCategoryIds(List.of(id));
        return bookDto;
    }
}
