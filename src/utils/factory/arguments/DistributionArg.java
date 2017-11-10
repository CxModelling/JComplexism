package utils.factory.arguments;

import org.json.JSONException;
import org.json.JSONObject;
import pcore.distribution.DistributionManager;
import pcore.distribution.IDistribution;
import utils.factory.Workshop;
import utils.json.FnJSON;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 *
 * Created by TimeWz on 2017/11/3.
 */
public class DistributionArg extends AbsArgument{
    public DistributionArg(String name) {
        super(name);
    }

    @Override
    public Class getType() {
        return IDistribution.class;
    }

    @Override
    public Object correct(Object value, Workshop ws) throws NoSuchElementException{
        if (value instanceof String) {
            try {
                if (ws.hasResource((String) value)) {
                    value = ws.getResource((String) value);
                } else {
                    value = DistributionManager.parseDistribution((String) value);
                }
            } catch (Exception e) {
                throw new NoSuchElementException("No such value");
            }
        }
        return value;
    }

    @Override
    public Object parse(String value) {
        return FnJSON.toDoubleMap(new JSONObject(value));
    }
}
