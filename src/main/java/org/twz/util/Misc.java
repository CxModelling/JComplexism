package org.twz.util;

public class Misc {
    public static double frame(double v, double l, double u) {
        if (v < l) return l;
        if (v > u) return u;
        return v;
    }
}
