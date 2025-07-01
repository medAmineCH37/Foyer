package tn.esprit.spring.exceptions;

public class ProfilingExecutionException extends RuntimeException {

    // Constructor with a custom message
    public ProfilingExecutionException(String message) {
        super(message);
    }

    // Constructor with a cause (original exception)
    public ProfilingExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
