package hexlet.code.app.exception;

public class EntityWithIdAlreadyExistsException extends RuntimeException {
    public EntityWithIdAlreadyExistsException(String message) {
        super(message);
    }
}
