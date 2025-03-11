package mate.academy.onlinebookstore.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERRORS = "errors";
    private static final String WHITESPACE = " ";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        Map<String, Object> body = getDefaultBody(HttpStatus.BAD_REQUEST);
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorMessage)
                .toList();
        body.put(ERRORS, errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(OrderProcessingException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundExceptions(
            OrderProcessingException ex
    ) {
        return getDefaultResponseEntity(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<Map<String, Object>> handleRegistrationExceptions(
            RegistrationException ex
    ) {
        return getDefaultResponseEntity(HttpStatus.CONFLICT, ex);
    }

    private String getErrorMessage(ObjectError objectError) {
        if (objectError instanceof FieldError fieldError) {
            String defaultMessage = fieldError.getDefaultMessage();
            String field = fieldError.getField();
            return field + WHITESPACE + defaultMessage;
        }
        return objectError.getDefaultMessage();
    }

    private Map<String, Object> getDefaultBody(HttpStatus httpStatus) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(STATUS, httpStatus);
        return body;
    }

    private ResponseEntity<Map<String, Object>> getDefaultResponseEntity(
            HttpStatus httpStatus,
            Exception ex
    ) {
        Map<String, Object> body = getDefaultBody(httpStatus);
        body.put(ERRORS, ex.getMessage());
        return new ResponseEntity<>(body, httpStatus);
    }
}
