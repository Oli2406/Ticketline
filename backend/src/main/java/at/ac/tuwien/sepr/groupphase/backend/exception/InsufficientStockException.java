package at.ac.tuwien.sepr.groupphase.backend.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }
}