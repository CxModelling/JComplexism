package hgm.validators;

import java.util.*;

/**
 * Validator for if a value in the set
 * Created by TimeWz on 2017/8/11.
 */
public class InSetValidator<T> implements IValidator<T> {

    private Set<T> Inclusion;
    private T Default;

    public InSetValidator(Collection<T> inc) {
        Inclusion = new HashSet<>(inc);
        Default = (new LinkedList<T>(inc)).getFirst();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void check(Object val) throws ValidationError {
        T obj;

        try {
            obj = (T) val;
        } catch (ClassCastException ex) {
            throw new ValidationError("Invalidate Object");
        }
        if (!Inclusion.contains(obj)) {
            throw new ValidationError("Invalidate Object");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T adjust(Object val) throws ValidationError {
        check(val);
        return (T) val;
    }

    @Override
    public T getDefault() {
        return Default;
    }
}
