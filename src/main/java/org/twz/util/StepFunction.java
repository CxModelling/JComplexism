package org.twz.util;

import org.mariuszgromada.math.mxparser.FunctionExtension;

public class StepFunction implements FunctionExtension {
    private double t0, ti, a, b;



    @Override
    public int getParametersNumber() {
        return 4;
    }

    @Override
    public void setParameterValue(int i, double v) {
        switch (i) {
            case 0:
                t0 = v;
                break;
            case 1:
                ti = v;
                break;
            case 2:
                a = v;
                break;
            case 3:
                b = v;
                break;
        }
    }

    @Override
    public String getParameterName(int i) {
        switch (i) {
            case 0:
                return "t0";
            case 1:
                return "time";
            case 2:
                return "value0";
            case 3:
                return "value1";
        }
        return null;
    }

    @Override
    public double calculate() {
        return (ti >= t0)? b: a;
    }

    @Override
    public FunctionExtension clone() {
        FunctionExtension clone;
        try {
            clone = (FunctionExtension) super.clone();
        } catch (CloneNotSupportedException e) {
            clone = this;
        }

        return clone;
    }
}
