package mate.academy.onlinebookstore.repository.book;

import java.util.List;
import java.util.Optional;
import mate.academy.onlinebookstore.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @EntityGraph(attributePaths = "categories")
    Optional<Book> findBookById(Long id);

    @Query(value = "SELECT bks.* FROM books bks INNER JOIN books_categories bc "
            + "on bks.id = bc.book_id WHERE bc.category_id = :id", nativeQuery = true)
    List<Book> findAllByCategoryId(@Param("id") Long id);

    @EntityGraph(attributePaths = "categories")
    Page<Book> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "categories")
    Page<Book> findAll(Specification<Book> specification, Pageable pageable);
}
