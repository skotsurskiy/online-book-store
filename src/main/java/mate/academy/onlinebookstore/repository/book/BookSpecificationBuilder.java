package mate.academy.onlinebookstore.repository.book;

import java.math.BigDecimal;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.BookSearchParameters;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.repository.SpecificationBuilder;
import mate.academy.onlinebookstore.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private static final String TITLE_KEY = "title";
    private static final String AUTHOR_KEY = "author";
    private static final String PRICE_KEY = "price";
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> specification = Specification.where(null);

        if (searchParameters.titles() != null && searchParameters.titles().length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider(TITLE_KEY)
                    .getSpecification(searchParameters.titles()));
        }
        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider(AUTHOR_KEY)
                    .getSpecification(searchParameters.authors()));
        }
        if (searchParameters.prices() != null && searchParameters.prices().length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider(PRICE_KEY)
                    .getSpecification(Arrays.stream(searchParameters
                            .prices())
                            .map(BigDecimal::toString)
                            .toArray(String[]::new)));
        }
        return specification;
    }
}
