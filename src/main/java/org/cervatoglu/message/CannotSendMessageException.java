package org.cervatoglu.message;

public class CannotSendMessageException extends RuntimeException {
    public CannotSendMessageException() {
        super();
    }

    public CannotSendMessageException(String message) {
        super(message);
    }

    public CannotSendMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotSendMessageException(Throwable cause) {
        super(cause);
    }
}
