package mate.academy.onlinebookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.onlinebookstore.dto.book.BookWithoutCategoryIdDto;
import mate.academy.onlinebookstore.dto.category.CategoryRequestDto;
import mate.academy.onlinebookstore.dto.category.CategoryResponseDto;
import org.junit.jupiter.api.AfterEach;
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
class CategoryControllerTest {
    public static final String URI_CATEGORIES = "/categories";
    public static final String URI_CATEGORIES_ID = "/categories/{id}";
    public static final String FIRST_CATEGORY = "firstCategory";
    public static final String SECOND_CATEGORY = "secondCategory";
    public static final long INVALID_ID = -1L;
    public static final String CATEGORY_NOT_FOUND_ERROR_MESSAGE = "Can't find category by id: -1";
    public static final String FIELD_PRICE = "price";
    public static final String URI_CATEGORIES_ID_BOOKS = "/categories/{id}/books";
    public static final long FIRST_ID_INDEX = 1L;
    public static final long SECOND_ID_INDEX = 2L;
    public static final String FIELD_ID = "id";
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

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
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
            using findAllCategories method to get list of all categoryResponseDtos
            """)
    @Sql(
            scripts = "classpath:database/categories/add-two-categories-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void findAllCategories_ReturnListOfCategoryResponseDtos() throws Exception {
        CategoryResponseDto firstCategory = new CategoryResponseDto(
                FIRST_ID_INDEX,
                FIRST_CATEGORY,
                FIRST_CATEGORY
        );
        CategoryResponseDto secondCategory = new CategoryResponseDto(
                SECOND_ID_INDEX,
                SECOND_CATEGORY,
                SECOND_CATEGORY
        );

        List<CategoryResponseDto> expected = List.of(firstCategory, secondCategory);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(URI_CATEGORIES)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<CategoryResponseDto> actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(expected);
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            using findCategoryById with valid id to get categoryResponseDto
            """)
    @Sql(
            scripts = "classpath:database/categories/add-two-categories-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void findCategoryById_WithValidId_ReturnResponseDto() throws Exception {
        CategoryResponseDto expected = new CategoryResponseDto(
                FIRST_ID_INDEX,
                FIRST_CATEGORY,
                FIRST_CATEGORY
        );

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(URI_CATEGORIES_ID, FIRST_ID_INDEX)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CategoryResponseDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CategoryResponseDto.class
        );

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            using findCategoryById with invalid id, expect EntityNotFoundException
            """)
    void findCategoryById_WithInvalidId_throwEntityNotFoundException() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(URI_CATEGORIES_ID, INVALID_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString())
                .contains(CATEGORY_NOT_FOUND_ERROR_MESSAGE);
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            using findBooksByCategoryId with valid id to get list of bookWithoutCategoryIdDtos
            """)
    @Sql(
            scripts = {
                    "classpath:database/books/add-five-books-to-books-table.sql",
                    "classpath:database/categories/add-two-categories-to-categories-table.sql",
                    "classpath:database/booksCategories/add-five-books-categories-relationships.sql"
            }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void findBooksByCategoryId_WithValidId_ReturnListOfBookWithoutCategoryIdDtos()
            throws Exception {
        BookWithoutCategoryIdDto firstBook = new BookWithoutCategoryIdDto(
                FIRST_ID_INDEX,
                "firstBook",
                "author",
                "8888",
                BigDecimal.valueOf(14.90),
                "firstBook",
                "coverImage"
        );
        BookWithoutCategoryIdDto secondBook = new BookWithoutCategoryIdDto(
                SECOND_ID_INDEX,
                "secondBook",
                "author",
                "7878",
                BigDecimal.valueOf(19.99),
                "secondBook",
                "coverImage"
        );

        List<BookWithoutCategoryIdDto> expected = List.of(firstBook, secondBook);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(URI_CATEGORIES_ID_BOOKS, FIRST_ID_INDEX)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<BookWithoutCategoryIdDto> actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {}
        );

        assertThat(actual)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields(FIELD_PRICE)
                .isEqualTo(expected);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @DisplayName("""
            using createCategory with valid role to create category, return CategoryResponseDto
            """)
    void createCategory_WithValidRole_ReturnCategoryResponseDto() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto(
                FIRST_CATEGORY,
                FIRST_CATEGORY
        );
        CategoryResponseDto expected = new CategoryResponseDto(
                FIRST_ID_INDEX,
                FIRST_CATEGORY,
                FIRST_CATEGORY
        );

        String json = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(URI_CATEGORIES)
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        CategoryResponseDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CategoryResponseDto.class
        );

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields(FIELD_ID)
                .isEqualTo(expected);
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            using createCategory with invalid role to create category, expect 403 status
            """)
    void createCategory_WithInvalidRole_ExpectForbiddenStatus() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto(
                FIRST_CATEGORY,
                FIRST_CATEGORY
        );

        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(URI_CATEGORIES)
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @DisplayName("""
            using updateCategory with valid role and valid id, return categoryResponseDto
            """)
    @Sql(
            scripts = "classpath:database/categories/add-one-category-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void updateCategory_WithValidRoleAndValidId_ReturnCategoryResponseDto() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto(
                SECOND_CATEGORY,
                SECOND_CATEGORY
        );
        CategoryResponseDto expected = new CategoryResponseDto(
                FIRST_ID_INDEX,
                SECOND_CATEGORY,
                SECOND_CATEGORY
        );

        String json = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(URI_CATEGORIES_ID, FIRST_ID_INDEX)
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CategoryResponseDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CategoryResponseDto.class
        );

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields(FIELD_ID)
                .isEqualTo(expected);
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            using updateCategory with invalid role to create category, expect 403 status
            """)
    @Sql(
            scripts = "classpath:database/categories/add-one-category-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void updateCategory_WithInvalidRole_ExpectForbiddenStatus() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto(
                SECOND_CATEGORY,
                SECOND_CATEGORY
        );

        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(URI_CATEGORIES_ID, FIRST_ID_INDEX)
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @DisplayName("""
            using updateCategory with invalid id, expect EntityNotFoundException
            """)
    void updateCategory_WithInvalidId_throwEntityNotFoundException() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto(
                SECOND_CATEGORY,
                SECOND_CATEGORY
        );

        String json = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(URI_CATEGORIES_ID, INVALID_ID)
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString())
                .contains(CATEGORY_NOT_FOUND_ERROR_MESSAGE);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @DisplayName("""
            using deleteCategory method with valid role, expect 204 status
            """)
    @Sql(
            scripts = "classpath:database/categories/add-one-category-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void deleteBook_WithValidRole_expectNoContentStatus() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(URI_CATEGORIES_ID, FIRST_ID_INDEX)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            using deleteCategory method with invalid role, expect 403 status
            """)
    @Sql(
            scripts = "classpath:database/categories/add-one-category-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void deleteBook_WithInvalidRole_expectForbiddenStatus() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(URI_CATEGORIES_ID, FIRST_ID_INDEX)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn();
    }
}
