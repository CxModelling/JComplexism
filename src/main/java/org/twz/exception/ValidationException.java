package org.twz.exception;

public class ValidationException extends Exception {
    private String Info;

    public ValidationException(String v) {
        Info = v;
    }

    public String toString() {
        return "Unmatched " + Info;
    }
}
