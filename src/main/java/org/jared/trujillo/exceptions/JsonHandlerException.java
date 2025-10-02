package org.jared.trujillo.exceptions;

public class JsonHandlerException extends RuntimeException {
    public JsonHandlerException(String message) {
        super(message);
    }
    public JsonHandlerException(String message, Throwable cause) { super(message, cause); }
}
