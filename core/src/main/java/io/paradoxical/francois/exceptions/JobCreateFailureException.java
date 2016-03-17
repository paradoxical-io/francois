package io.paradoxical.francois.exceptions;

public class JobCreateFailureException extends Exception {
    public JobCreateFailureException(final Throwable cause) {
        super(cause);
    }

    public JobCreateFailureException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JobCreateFailureException(final String message) {
        super(message);
    }
}
