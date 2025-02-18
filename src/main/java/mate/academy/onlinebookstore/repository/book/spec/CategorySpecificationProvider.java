package mate.academy.onlinebookstore.repository.book.spec;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.Category;
import mate.academy.onlinebookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CategorySpecificationProvider implements SpecificationProvider<Book> {
    private static final String CATEGORY = "category";

    @Override
    public String getKey() {
        return CATEGORY;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            Join<Book, Category> categories = root.join("categories", JoinType.INNER);
            return categories.get("id").in((Object[]) params);
        };
    }
}
