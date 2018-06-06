package org.twz.cx.abmodel.behaviour.modifier;

import org.twz.statespace.Transition;
import org.json.JSONObject;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 2017/9/7.
 */
public class ModifierSet {
    private LinkedHashMap<String, AbsModifier> Mods;
    public ModifierSet() {
        Mods = new LinkedHashMap<>();
    }

    public void append(AbsModifier mod) {
        Mods.putIfAbsent(mod.getName(), mod);

    }

    public AbsModifier getModifier(String name) {
        return Mods.get(name);
    }

    public Collection<AbsModifier> on(Transition tr) {
        return Mods.values().stream()
                .filter(e -> e.getTarget() == tr)
                .collect(Collectors.toList());
    }

    public JSONObject toJSON() {
        JSONObject js = new JSONObject();
        Mods.entrySet().forEach(e -> js.put(e.getKey(), e.getValue().getValue()));
        return js;
    }
}
