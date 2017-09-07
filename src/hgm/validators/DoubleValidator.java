package hgm.validators;

/**
 * Validator for single number
 * Created by TimeWz on 2017/8/11.
 */
public class DoubleValidator implements IValidator<Double> {
    private final double Lower, Upper, Default;

    public DoubleValidator(double lo, double up) {
        Lower = lo;
        Upper = up;
        Default = Math.min(Math.max(0, lo), up);
    }

    public DoubleValidator() {
        this(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    @Override
    public void check(Object val) throws ValidationError {
        double v;
        try {
            v = (Double) val;
        } catch (ClassCastException e) {
            throw new ValidationError("Not a double");
        }
        if (v < Lower) throw new ValidationError("Illegal low value");
        if (v > Upper) throw new ValidationError("Illegal high value");
    }

    @Override
    public Double adjust(Object val) throws ValidationError {
        double v;
        try {
            v = (Double) val;
        } catch (ClassCastException e) {
            return getDefault();
        }
        if (v < Lower) return Lower;
        if (v > Upper) return Upper;
        return v;
    }

    @Override
    public Double getDefault() {
        return Default;
    }

}
