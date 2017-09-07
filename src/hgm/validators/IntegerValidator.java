package hgm.validators;

/**
 * Validator for integer value
 * Created by TimeWz on 2017/8/23.
 */
public class IntegerValidator implements IValidator<Integer> {
    private final int Lower, Upper, Default;

    public IntegerValidator(int lo, int up) {
        Lower = lo;
        Upper = up;
        Default = Math.min(Math.max(0, lo), up);
    }

    public IntegerValidator() {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public void check(Object val) throws ValidationError {
        int v;
        try {
            v = (Integer) val;
        } catch (ClassCastException e) {
            throw new ValidationError("Not a integer");
        }
        if (v < Lower) throw new ValidationError("Illegal low value");
        if (v > Upper) throw new ValidationError("Illegal high value");
    }

    @Override
    public Integer adjust(Object val) throws ValidationError {
        int v;
        try {
            v = (Integer) val;
        } catch (ClassCastException e) {
            return getDefault();
        }
        if (v < Lower) return Lower;
        if (v > Upper) return Upper;
        return v;
    }

    @Override
    public Integer getDefault() {
        return Default;
    }
}
