package org.twz.exception;

public class IncompleteConditionException extends Exception {
    private String Info;

    public IncompleteConditionException(String v) {
        Info = v;
    }

    public String toString() {
        return "Condition " + Info + " undefined";
    }
}
