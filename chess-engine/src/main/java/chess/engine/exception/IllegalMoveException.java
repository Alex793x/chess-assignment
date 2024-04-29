package chess.engine.exception;

public class IllegalMoveException extends RuntimeException {
    // Constructor that accepts only the error message
    public IllegalMoveException(String message) {
        super(message);
    }

    // Constructor that accepts both the message and cause of the exception
    public IllegalMoveException(String message, Throwable cause) {
        super(message, cause);
    }
}