package org.twz.dag.actor;

import java.util.List;

/**
 * Blueprint of a actor
 * Created by TimeWz on 08/08/2018.
 */
public class ActorBlueprint {
    public static final String Compound = "c", Single = "s", Frozen = "f", None = "n";

    public String Actor, Type;
    public List<String> Flow, Requirement;

    public ActorBlueprint(String actor, String type, List<String> flow, List<String> requirement) {
        Actor = actor;
        Type = type;
        Flow = flow;
        Requirement = requirement;
    }

    public ActorBlueprint(String actor, String type) {
        this(actor, type, null, null);
    }

    public void setType(String type) {
        Type = type;
    }

    @Override
    public String toString() {
        if (Flow.size() > 0) {
            return String.format("%s, %s, %s", Actor, Type, String.join("|", Flow));
        } else {
            return String.format("%s, %s", Actor, Type);
        }

    }
}
