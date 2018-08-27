package org.twz.cx.mcore.communicator;

import org.json.JSONObject;
import org.twz.cx.element.Disclosure;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.dataframe.Pair;

public class ValueImpulseShocker extends AbsShocker {
    private String Target;
    private String Value;

    public ValueImpulseShocker(String target, String value) {
        Target = target;
        Value = value;
    }

    public ValueImpulseShocker(String target) {
        this(target, "v1");
    }

    public ValueImpulseShocker(JSONObject js) {
        this(js.getString("Target"), js.getString("Value"));
    }

    @Override
    public Pair<String, JSONObject> shock(Disclosure dis, AbsSimModel source, AbsSimModel target, double time) {
        JSONObject js = new JSONObject();
        js.put("k", Target);
        js.put("v", dis.getDouble(this.Value));
        return new Pair<>("impulse", js);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject js = super.toJSON();
        js.put("Value", Value);
        js.put("Target", Target);
        return js;
    }
}
