package hgm.validators;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Validator for if a value in the set
 * Created by TimeWz on 2017/8/11.
 */
public class InSetValidator implements IValidator {

    private Set<Object> Inclusion;

    public InSetValidator(Collection<Object> inc) {
        Inclusion = new HashSet<>(inc);
    }

    public InSetValidator() {
        Inclusion = new HashSet<>();
    }

    public void renewInclusion(Collection<Object> inc) {
        Inclusion.clear();
        Inclusion.addAll(inc);
    }

    @Override
    public void Check(Object val) throws ValidationError {
        if (!Inclusion.contains(val)) throw new ValidationError("Invalidate Object");

    }
}
