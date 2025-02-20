package mate.academy.onlinebookstore.dto.cartitem;

import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequestDto {
    private Long bookId;
    @Max(99)
    private int quantity;
}
