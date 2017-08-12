package test;


import hgm.validators.NumberValidator;
import hgm.validators.ValidationError;
import org.junit.Test;
/**
 *
 * Created by TimeWz on 2017/8/11.
 */
public class ValidationTest {
    @Test
    public void validateNumber() {
        NumberValidator Vld = new NumberValidator(0, 5);

        try {
            Vld.check(1);
        } catch (ValidationError validationError) {
            validationError.printStackTrace();
        }

        try {
            Vld.check(-1);
        } catch (ValidationError validationError) {
            validationError.printStackTrace();
        }

        try {
            Vld.check(10);
        } catch (ValidationError validationError) {
            validationError.printStackTrace();
        }


    }
}
