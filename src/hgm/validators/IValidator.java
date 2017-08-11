package hgm.validators;

/**
 *
 * Created by TimeWz on 2017/8/11.
 */
public interface IValidator {
    void Check(Object val) throws ValidationError;
}
