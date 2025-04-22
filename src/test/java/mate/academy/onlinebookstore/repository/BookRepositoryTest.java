package mate.academy.onlinebookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.repository.book.BookRepository;
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
                "classpath:database/books/add-five-books-to-books-table.sql",
                "classpath:database/categories/add-two-categories-to-categories-table.sql",
                "classpath:database/booksCategories/add-five-books-categories-relationships.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/delete-all-from-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void findAllByCategoryId_WhereCategoryIdIsOne_ReturnsTwoBooks() {
        List<Book> actual = bookRepository.findAllByCategoryId(
                1L, PageRequest.of(0, 10)
        ).toList();
        assertEquals(2, actual.size());
        assertEquals("firstBook", actual.get(0).getTitle());
        assertEquals("secondBook", actual.get(1).getTitle());
    }
}
