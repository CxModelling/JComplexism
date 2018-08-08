package org.twz.dag.actor;

import java.util.List;

/**
 * Blueprint of a actor
 * Created by TimeWz on 08/08/2018.
 */
public class ActorBlueprint {
    public String Actor, Type, TypeH;
    public List<String> Flow;

    public ActorBlueprint(String actor, String type, String typeH, List<String> flow) {
        Actor = actor;
        Type = type;
        TypeH = typeH;
        Flow = flow;
    }

    public ActorBlueprint(String actor, String type, String typeH) {
        this(actor, type, typeH, null);
    }
}
