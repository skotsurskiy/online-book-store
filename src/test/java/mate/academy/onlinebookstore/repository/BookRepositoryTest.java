package mate.academy.onlinebookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.repository.book.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("""
            Find all books by category id where category id is 1
            """)
    @Sql(
            scripts = {
                    "classpath:database/books/add-three-books-to-books-table.sql",
                    "classpath:database/categories/add-two-categories-to-categories-table.sql",
                    "classpath:database/booksCategories/"
                            + "add-three-books-categories-relationships.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/delete-all-from-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void findAllByCategoryId_WhereCategoryIdIsOne_ReturnsTwoBooks() {
        List<Book> expected = List.of(
                createBook("firstBook", "1111-2222-3333-4444"),
                createBook("secondBook", "4444-5555-6666-7777")
        );

        List<Book> actual = bookRepository.findAllByCategoryId(
                1L, PageRequest.of(0, 10)
        ).toList();
        assertEquals(2, actual.size());
        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id", "categories")
                .isEqualTo(expected);
    }

    private Book createBook(String title, String isbn) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor("author");
        book.setPrice(BigDecimal.valueOf(19.99));
        book.setIsbn(isbn);
        book.setDescription("description");
        book.setCoverImage("coverImage");
        book.setCategories(Set.of());
        return book;
    }
}
