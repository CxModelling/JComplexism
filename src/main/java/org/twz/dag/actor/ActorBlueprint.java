package org.twz.dag.actor;

import java.util.List;

/**
 * Blueprint of a actor
 * Created by TimeWz on 08/08/2018.
 */
public class ActorBlueprint {
    public static final String Compound = "c", Single = "s", Frozen = "f";

    public final String Actor, Type;
    public List<String> Flow;

    public ActorBlueprint(String actor, String type, List<String> flow) {
        Actor = actor;
        Type = type;
        Flow = flow;
    }

    public ActorBlueprint(String actor, String type) {
        this(actor, type, null);
    }
}
