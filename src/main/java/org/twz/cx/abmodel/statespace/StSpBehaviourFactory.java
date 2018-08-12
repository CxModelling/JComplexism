package org.twz.cx.abmodel.statespace;

import org.json.JSONObject;
import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.abmodel.statespace.behaviour.FDShock;
import org.twz.cx.abmodel.statespace.behaviour.Reincarnation;
import org.twz.cx.abmodel.statespace.behaviour.StateTrack;
import org.twz.factory.Workshop;
import org.twz.factory.arguments.AbsArgument;
import org.twz.factory.arguments.OptionArg;
import org.twz.statespace.State;
import org.twz.statespace.Transition;

public class StSpBehaviourFactory {
    private static Workshop<AbsBehaviour> Factory = new Workshop<>();

    static {

        AbsArgument[] ags;

        ags = new AbsArgument[]{new OptionArg("s_src", "States", State.class),
                new OptionArg("t_tar", "Transitions", Transition.class)};
        Factory.register("FDShock", FDShock.class, ags);

        ags = new AbsArgument[]{new OptionArg("s_death", "States", State.class),
                new OptionArg("s_birth", "States", State.class)};
        Factory.register("Reincarnation", Reincarnation.class, ags);

        ags = new AbsArgument[]{new OptionArg("s_src", "States", State.class)};
        Factory.register("StateTrack", StateTrack.class, ags);

    }

    public static void appendResouce(String key, Object res) {
        Factory.appendResource(key, res);
    }

    public static void clearResource() {
        Factory.clearResource();
    }

    public static AbsBehaviour create(JSONObject js) {
        return Factory.create(js);
    }
}
