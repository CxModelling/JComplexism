package hgm.abmodel.trait;

import org.json.JSONArray;
import org.json.JSONString;
import utils.factory.Workshop;
import utils.factory.arguments.AbsArgument;
import utils.factory.arguments.DistributionArg;

import java.util.*;

/**
 *
 * Created by TimeWz on 2017/10/10.
 */
public class TraitSet implements JSONString {
    private static Workshop<ITrait> Fact = new Workshop<>();

    static {
        Fact.register("Distribution", DistributionTrait.class,
                new AbsArgument[]{new DistributionArg("dist")});
    }

    private List<ITrait> Traits;

    public TraitSet() {
        Traits = new ArrayList<>();
    }

    public void append(ITrait trait) {
        Traits.add(trait);
    }

    public Map<String, Object> fill(Map<String, Object> info) {
        Map<String, Object> res = new HashMap<>(info);
        for (ITrait trait: Traits) {
            trait.fill(res);
        }
        return res;

    }

    public JSONArray toJSON() {
        JSONArray json = new JSONArray();
        for (ITrait trait: Traits) {
            json.put(trait.toJSON());
        }
        return json;
    }

    @Override
    public String toJSONString() {
        return toJSON().toString();
    }
}
