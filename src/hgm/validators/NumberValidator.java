package hgm.validators;

/**
 * Validator for single number
 * Created by TimeWz on 2017/8/11.
 */
public class NumberValidator implements IValidator {
    private final double Lower, Upper;

    public NumberValidator(double lo, double up) {
        Lower = lo;
        Upper = up;
    }

    public NumberValidator() {
        this(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    @Override
    public void Check(Object val) throws ValidationError {
        double v;
        try {
            v = (double) val;
        } catch (ClassCastException e) {
            throw new ValidationError("Not a number");
        }
        if (v < Lower) throw new ValidationError("Illegal low value");
        if (v > Upper) throw new ValidationError("Illegal high value");
    }

}
