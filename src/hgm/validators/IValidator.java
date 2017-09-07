package hgm.validators;

/**
 *
 * Created by TimeWz on 2017/8/11.
 */
public interface IValidator<T> {
    void check(Object val) throws ValidationError;
    T adjust(Object val) throws ValidationError;
    T getDefault();
}
