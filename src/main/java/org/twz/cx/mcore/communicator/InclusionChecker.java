package org.twz.cx.mcore.communicator;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.element.Disclosure;
import org.twz.io.FnJSON;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class InclusionChecker extends AbsChecker {

    private Set<String> Inclusion;

    public InclusionChecker(Collection<String> inclusion) {
        Inclusion = new HashSet<>(inclusion);
    }

    public InclusionChecker(String[] inclusion) {
        Inclusion = new HashSet<>();
        Inclusion.addAll(Arrays.asList(inclusion));
    }

    public InclusionChecker(JSONObject js) throws JSONException {
        Inclusion = FnJSON.toStringSet(js.getJSONArray("Inclusion"));
    }

    @Override
    public boolean check(Disclosure dis) {
        return Inclusion.contains(dis.What);
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = super.toJSON();
        js.put("Inclusion", Inclusion);
        return js;
    }

    @Override
    public AbsChecker deepcopy() {
        return new InclusionChecker(Inclusion);
    }
}
