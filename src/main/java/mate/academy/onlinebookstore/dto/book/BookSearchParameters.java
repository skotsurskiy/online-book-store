package mate.academy.onlinebookstore.dto.book;

import java.math.BigDecimal;

public record BookSearchParameters(String[] titles, String[] authors, BigDecimal[] prices) {
}
