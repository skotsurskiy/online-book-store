package mate.academy.onlinebookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.onlinebookstore.dto.book.BookDto;
import mate.academy.onlinebookstore.dto.book.CreateBookRequestDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    public static final String FIRST_BOOK_TITLE = "firstBook";
    public static final String SECOND_BOOK_TITLE = "secondBook";
    public static final String THIRD_BOOK_TITLE = "thirdBook";
    public static final String AUTHOR = "author";
    public static final BigDecimal PRICE = BigDecimal.valueOf(19.99);
    public static final String DESCRIPTION = "description";
    public static final String COVER_IMAGE = "coverImage";
    public static final String URI_BOOKS = "/books";
    public static final String URI_BOOKS_ID = "/books/{id}";
    public static final long INITIAL_ID = 1L;
    public static final String FIELD_ID = "id";
    public static final String BOOK_NOT_FOUND_ERROR_MESSAGE = "Can't find book by id: -1";
    public static final String ISBN = "1111-2222-3333-4444";
    public static final long INVALID_ID = -1L;
    public static final String URI_BOOKS_SEARCH = "/books/search";
    public static final String PARAM_TITLES = "titles";
    public static final int FIRST_LIST_INDEX = 0;
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext webApplicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/delete-all-from-tables.sql")
            );
        }
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            using findAllBooks method with valid role to get list of all books
            """)
    @Sql(
            scripts = "classpath:database/books/add-three-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/books/delete-all-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void findAllBooks_WithValidRole_ReturnListOfBookDtos() throws Exception {
        List<BookDto> expected = List.of(
                createBookDto(FIRST_BOOK_TITLE),
                createBookDto(SECOND_BOOK_TITLE),
                createBookDto(THIRD_BOOK_TITLE)
        );

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URI_BOOKS)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<BookDto> actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        assertEquals(expected.size(), actual.size());
        assertThat(actual.get(FIRST_LIST_INDEX))
                .usingRecursiveComparison()
                .ignoringFields(FIELD_ID)
                .isEqualTo(expected.get(FIRST_LIST_INDEX));
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            using findBookById method with valid id get bookDto
            """)
    @Sql(
            scripts = "classpath:database/books/add-three-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/books/delete-all-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void findBookById_WithValidId_ReturnBookDto() throws Exception {
        BookDto expected = createBookDto(FIRST_BOOK_TITLE);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(URI_BOOKS_ID, INITIAL_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                BookDto.class
        );

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields(FIELD_ID)
                .isEqualTo(expected);
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            using findBookById method with invalid id get bookDto, throw EntityNotFoundException
            """)
    @Sql(
            scripts = "classpath:database/books/add-three-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/books/delete-all-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void findBookById_WithInvalidId_ThrowEntityNotFoundException() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(URI_BOOKS_ID, INVALID_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        String errorMessage = mvcResult.getResponse().getContentAsString();

        assertThat(errorMessage).contains(BOOK_NOT_FOUND_ERROR_MESSAGE);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("""
            using createBook method to create book with valid role, return bookDto
            """)
    @Sql(
            scripts = "classpath:database/categories/add-one-category-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/delete-all-from-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void createBook_WithCreateBookRequestDtoAndValidRole_ReturnBookDto() throws Exception {
        CreateBookRequestDto requestDto = createBookRequestDto(FIRST_BOOK_TITLE);
        BookDto expected = createBookDto(requestDto.getTitle());
        expected.setIsbn(requestDto.getIsbn());
        expected.setCategoryIds(List.of(INITIAL_ID));

        String json = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(URI_BOOKS)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                BookDto.class
        );

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields(FIELD_ID)
                .isEqualTo(expected);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("""
            using deleteBook method with valid id, expect 204 status
            """)
    @Sql(
            scripts = "classpath:database/books/add-one-book-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void deleteBook_WithValidId_expectNoContentStatus() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .delete(URI_BOOKS_ID, INITIAL_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("""
            using updateBook method with valid id and requestDto to update book,
            update book and return bookDto
            """)
    @Sql(
            scripts = {
                    "classpath:database/books/add-one-book-to-books-table.sql",
                    "classpath:database/categories/add-one-category-to-categories-table.sql",
                    "classpath:database/booksCategories/add-one-book-category-relationship.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/delete-all-from-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void updateBook_WithCreateRequestDtoAndValidId_UpdateBookAndReturnBookDto() throws Exception {
        CreateBookRequestDto requestDto = createBookRequestDto(SECOND_BOOK_TITLE);
        BookDto expected = createBookDto(SECOND_BOOK_TITLE);
        expected.setIsbn(requestDto.getIsbn());
        expected.setCategoryIds(requestDto.getCategoryIds());

        String json = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.put(URI_BOOKS_ID, INITIAL_ID)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                BookDto.class
        );

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields(FIELD_ID)
                .isEqualTo(expected);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("""
            using updateBook method with invalid id, throw EntityNotFoundException
            """)
    void updateBook_WithInvalidId_throwEntityNotFoundException() throws Exception {
        CreateBookRequestDto requestDto = createBookRequestDto(SECOND_BOOK_TITLE);
        String json = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.put(URI_BOOKS_ID, INVALID_ID)
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        String actual = mvcResult.getResponse().getContentAsString();

        assertThat(actual).contains(BOOK_NOT_FOUND_ERROR_MESSAGE);
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            using search method, return list of two books with different title search parameters
            """)
    @Sql(scripts = {
            "classpath:database/books/add-three-books-to-books-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/delete-all-from-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void search_WithSearchParameters_returnListOfTwoBooks() throws Exception {
        List<BookDto> expected = List.of(
                createBookDto(FIRST_BOOK_TITLE),
                createBookDto(THIRD_BOOK_TITLE)
        );

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(URI_BOOKS_SEARCH)
                                .param(PARAM_TITLES, FIRST_BOOK_TITLE, THIRD_BOOK_TITLE)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<BookDto> actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        assertEquals(expected.size(), actual.size());
        assertThat(actual.get(FIRST_LIST_INDEX))
                .usingRecursiveComparison()
                .ignoringFields(FIELD_ID)
                .isEqualTo(expected.get(FIRST_LIST_INDEX));
    }

    private BookDto createBookDto(String title) {
        BookDto bookDto = new BookDto();
        bookDto.setTitle(title);
        bookDto.setAuthor(AUTHOR);
        bookDto.setPrice(PRICE);
        bookDto.setIsbn(ISBN);
        bookDto.setDescription(DESCRIPTION);
        bookDto.setCoverImage(COVER_IMAGE);
        bookDto.setCategoryIds(List.of());
        return bookDto;
    }

    private CreateBookRequestDto createBookRequestDto(String title) {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(title);
        requestDto.setAuthor(AUTHOR);
        requestDto.setPrice(PRICE);
        requestDto.setIsbn(ISBN);
        requestDto.setCategoryIds(List.of(INITIAL_ID));
        requestDto.setDescription(DESCRIPTION);
        requestDto.setCoverImage(COVER_IMAGE);
        return requestDto;
    }
}
