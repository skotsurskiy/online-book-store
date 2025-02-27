package mate.academy.onlinebookstore.exception;

public class InvalidOrderStatusException extends RuntimeException {
    public InvalidOrderStatusException(String message) {
        super(message);
    }
}
