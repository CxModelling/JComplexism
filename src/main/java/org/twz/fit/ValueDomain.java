package org.twz.fit;

public class ValueDomain {
    public final String Name, Type;
    public final Double Lower, Upper;

    public ValueDomain(String name, String type, Double lower, Double upper) {
        Name = name;
        Type = type;
        Lower = lower;
        Upper = upper;
    }
}
