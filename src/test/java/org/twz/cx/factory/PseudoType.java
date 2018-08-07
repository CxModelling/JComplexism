package org.twz.cx.factory;

/**
 * Created by TimeWz on 2017/11/3.
 */
public class PseudoType {
    private String Name;
    private String S;
    private Integer I;
    private Double D;

    public PseudoType(String name, String s, Integer i, Double d) {
        Name = name;
        S = s;
        I = i;
        D = d;
    }

    @Override
    public String toString() {
        return "PseudoType{" +
                "Name='" + Name + '\'' +
                ", S='" + S + '\'' +
                ", I=" + I +
                ", D=" + D +
                '}';
    }
}
