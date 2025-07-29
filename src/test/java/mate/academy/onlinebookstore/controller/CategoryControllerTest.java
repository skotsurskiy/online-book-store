package mate.academy.onlinebookstore.controller;

import static mate.academy.onlinebookstore.util.TestUtil.CATEGORY_NOT_FOUND_ERROR_MESSAGE;
import static mate.academy.onlinebookstore.util.TestUtil.FIELD_ID;
import static mate.academy.onlinebookstore.util.TestUtil.FIRST_CATEGORY_TITLE;
import static mate.academy.onlinebookstore.util.TestUtil.FIRST_VALID_ID;
import static mate.academy.onlinebookstore.util.TestUtil.INVALID_ID;
import static mate.academy.onlinebookstore.util.TestUtil.SECOND_CATEGORY_TITLE;
import static mate.academy.onlinebookstore.util.TestUtil.SECOND_VALID_ID;
import static mate.academy.onlinebookstore.util.TestUtil.URI_CATEGORIES;
import static mate.academy.onlinebookstore.util.TestUtil.URI_CATEGORIES_ID;
import static mate.academy.onlinebookstore.util.TestUtil.URI_CATEGORIES_ID_BOOKS;
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
                FIRST_VALID_ID,
                FIRST_CATEGORY_TITLE,
                FIRST_CATEGORY_TITLE
        );
        CategoryResponseDto secondCategory = new CategoryResponseDto(
                SECOND_VALID_ID,
                SECOND_CATEGORY_TITLE,
                SECOND_CATEGORY_TITLE
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

        assertEquals(expected, actual);
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
                FIRST_VALID_ID,
                FIRST_CATEGORY_TITLE,
                FIRST_CATEGORY_TITLE
        );

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(URI_CATEGORIES_ID, FIRST_VALID_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CategoryResponseDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CategoryResponseDto.class
        );

        assertEquals(expected, actual);
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
                    "classpath:database/books/add-three-books-to-books-table.sql",
                    "classpath:database/categories/add-two-categories-to-categories-table.sql",
                    "classpath:database/booksCategories/"
                            + "add-three-books-categories-relationships.sql"
            }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void findBooksByCategoryId_WithValidId_ReturnListOfBookWithoutCategoryIdDtos()
            throws Exception {
        BookWithoutCategoryIdDto firstBook = new BookWithoutCategoryIdDto(
                FIRST_VALID_ID,
                "firstBook",
                "author",
                "1111-2222-3333-4444",
                BigDecimal.valueOf(19.99),
                "description",
                "coverImage"
        );
        BookWithoutCategoryIdDto secondBook = new BookWithoutCategoryIdDto(
                SECOND_VALID_ID,
                "secondBook",
                "author",
                "4444-5555-6666-7777",
                BigDecimal.valueOf(19.99),
                "description",
                "coverImage"
        );

        List<BookWithoutCategoryIdDto> expected = List.of(firstBook, secondBook);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(URI_CATEGORIES_ID_BOOKS, FIRST_VALID_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<BookWithoutCategoryIdDto> actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {}
        );

        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @DisplayName("""
            using createCategory with valid role to create category, return CategoryResponseDto
            """)
    void createCategory_WithValidRole_ReturnCategoryResponseDto() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto(
                FIRST_CATEGORY_TITLE,
                FIRST_CATEGORY_TITLE
        );
        CategoryResponseDto expected = new CategoryResponseDto(
                FIRST_VALID_ID,
                FIRST_CATEGORY_TITLE,
                FIRST_CATEGORY_TITLE
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
                SECOND_CATEGORY_TITLE,
                SECOND_CATEGORY_TITLE
        );
        CategoryResponseDto expected = new CategoryResponseDto(
                FIRST_VALID_ID,
                SECOND_CATEGORY_TITLE,
                SECOND_CATEGORY_TITLE
        );

        String json = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(URI_CATEGORIES_ID, FIRST_VALID_ID)
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
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @DisplayName("""
            using updateCategory with invalid id, expect EntityNotFoundException
            """)
    void updateCategory_WithInvalidId_throwEntityNotFoundException() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto(
                SECOND_CATEGORY_TITLE,
                SECOND_CATEGORY_TITLE
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
                                .delete(URI_CATEGORIES_ID, FIRST_VALID_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn();
    }
}
