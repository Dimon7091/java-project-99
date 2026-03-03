package hexlet.code.app.exception;

public class StatusSlugAlreadyExistsException extends RuntimeException {
    public StatusSlugAlreadyExistsException(String message) {
        super(message);
    }
}
