package test;


import hgm.validators.DoubleValidator;
import hgm.validators.IntegerValidator;
import hgm.validators.ValidationError;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * Created by TimeWz on 2017/8/11.
 */
public class ValidationTest {
    @Test
    public void validateNumber() {
        DoubleValidator Vld = new DoubleValidator(0, 5);

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


    public class AA {
        private int I;
        private int D;
        public AA(int i, int d) {
            I = i;
            D = d;
        }
        public String toString() {
            return ""+ I +", " + D;
        }
    }
    @Test
    public void constructor() {

        System.out.println(new JSONArray("{2,3,4}"));


    }
}
