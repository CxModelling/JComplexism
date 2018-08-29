package org.twz.cx.abmodel.statespace;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.statespace.AbsStateSpace;

public class StSpBehaviourFactoryTest {
    private Director Da;
    private AbsStateSpace DC;

    @Before
    public void setUp() throws JSONException {
        Da = new Director();
        Da.loadBayesNet("src/test/resources/script/pBAD.txt");
        Da.loadStateSpace("src/test/resources/script/BAD.txt");

        DC = Da.generateDCore("BAD", "pBAD");

        StSpBehaviourFactory.appendResource("States", DC.getStateSpace());
        StSpBehaviourFactory.appendResource("Transitions", DC.getTransitionSpace());
    }

    @Test
    public void getReincarnation() throws JSONException {
        JSONObject js = new JSONObject("{'Name': 'life', 'Type': 'Reincarnation', " +
                "'Args': {'s_death': 'Dead', 's_birth': 'Young'}}");
        AbsBehaviour Be = StSpBehaviourFactory.create(js);
        System.out.println(Be.toJSON());
    }

    @Test
    public void getFDShock() throws JSONException {
        JSONObject js = new JSONObject("{'Name': 'foi', 'Type': 'FDShock', " +
                "'Args': {'s_src': 'Middle', 't_tar': 'Die'}}");
        AbsBehaviour Be = StSpBehaviourFactory.create(js);
        System.out.println(Be.toJSON());
    }
}