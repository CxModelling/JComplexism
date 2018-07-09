package org.twz.cx.abmodel;

class NameGenerator implements Cloneable {
    private String Prefix;
    private int Ini, Step;

    public NameGenerator(String prefix, int ini, int step) {
        Prefix = prefix;
        Ini = ini;
        Step = step;
    }

    public NameGenerator(String prefix) {
        this(prefix, 1, 1);
    }

    public NameGenerator() {
        this("Ag");
    }

    public String getNext() {
        int next = Ini;
        Ini += Step;
        return Prefix + next;
    }

    public NameGenerator clone() {
        NameGenerator ng = null;
        try {
            ng = (NameGenerator) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return ng;
    }
}
