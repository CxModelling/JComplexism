package hgm.abmodel.trait;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import pcore.distribution.DistributionManager;
import pcore.distribution.IDistribution;

/**
 *
 * Created by TimeWz on 2017/8/12.
 */
public class DistributionTrait implements ITrait {
    private String Name;
    private IDistribution Dist;

    public DistributionTrait(JSONObject js) {
        this(js.getString("Name"), js.getString("Distribution"));
    }

    public DistributionTrait(String name, String dist) {
        this(name, DistributionManager.parseDistribution(dist));
    }

    public DistributionTrait(String name, IDistribution dist) {
        Name = name;
        Dist = dist;
    }

    @Override
    public void fill(Map<String, Object> info) {
        info.putIfAbsent(Name, Dist.sample());
    }

    @Override
    public JSONArray toJSON() {
        // todo
        return null;
    }


}
