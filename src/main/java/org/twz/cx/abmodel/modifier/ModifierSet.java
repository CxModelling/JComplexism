package org.twz.cx.abmodel.modifier;

import org.twz.statespace.Transition;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by timewz on 30/09/17.
 */
public class ModifierSet {
    private LinkedHashMap<String, AbsModifier> Mods;

    public ModifierSet() {
        Mods = new LinkedHashMap<>();
    }

    public void put(String name, AbsModifier mod) {
        Mods.put(name, mod);
    }

    public AbsModifier get(String name) {
        return Mods.get(name);
    }

    public List<AbsModifier> on(Transition tr) {
        return Mods.values().stream().filter(e->e.getTarget()==tr).collect(Collectors.toList());
    }

    public JSONObject toJSON() {
        return new JSONObject(Mods.values().stream()
                .collect(Collectors.toMap(AbsModifier::getName, AbsModifier::getValue)));
    }
}
