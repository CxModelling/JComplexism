package org.twz.cx.abmodel.statespace;

import org.json.JSONObject;
import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.abmodel.statespace.behaviour.*;
import org.twz.factory.Workshop;
import org.twz.factory.arguments.*;
import org.twz.statespace.State;
import org.twz.statespace.Transition;

public class StSpBehaviourFactory {
    private static Workshop<AbsBehaviour> Factory = new Workshop<>();

    static {

        AbsArgument[] ags;

        ags = new AbsArgument[]{new OptionArg("s_src", "States", State.class),
                new OptionArg("t_tar", "Transitions", Transition.class)};
        Factory.register("FDShock", FDShock.class, ags);

        ags = new AbsArgument[]{new OptionArg("s_src", "States", State.class),
                new OptionArg("t_tar", "Transitions", Transition.class)};
        Factory.register("DDShock", DDShock.class, ags);

        ags = new AbsArgument[]{new OptionArg("t_tar", "Transitions", Transition.class)};
        Factory.register("ExternalShock", ExternalShock.class, ags);

        ags = new AbsArgument[]{new OptionArg("s_death", "States", State.class),
                new OptionArg("s_birth", "States", State.class)};
        Factory.register("Reincarnation", Reincarnation.class, ags);

        ags = new AbsArgument[]{new OptionArg("s_death", "States", State.class),
                new OptionArg("s_birth", "States", State.class),
                new PositiveDoubleArg("rate"), new PositiveDoubleArg("dt")};
        Factory.register("LifeRate", LifeRate.class, ags);

        ags = new AbsArgument[]{new OptionArg("s_death", "States", State.class),
                new OptionArg("s_birth", "States", State.class),
                new PositiveDoubleArg("cap"), new PositiveDoubleArg("rate"), new PositiveDoubleArg("dt")};
        Factory.register("LifeS", LifeS.class, ags);

        ags = new AbsArgument[]{new OptionArg("s_death", "States", State.class)};
        Factory.register("Cohort", Cohort.class, ags);

        ags = new AbsArgument[]{new OptionArg("s_src", "States", State.class)};
        Factory.register("StateTrack", StateTrack.class, ags);

        ags = new AbsArgument[]{new OptionArg("s_birth", "States", State.class)};
        Factory.register("AgentImport", AgentImport.class, ags);

        ags = new AbsArgument[]{new StringArg("key"), new PositiveDoubleArg("dt")};
        Factory.register("TimeVaryingSS", TimeVaryingSS.class, ags);

    }

    public static void appendResource(String key, Object res) {
        Factory.appendResource(key, res);
    }

    public static void clearResource() {
        Factory.clearResource();
    }

    public static AbsBehaviour create(JSONObject js) {
        return Factory.create(js);
    }
}
