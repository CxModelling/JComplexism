package org.twz.cx.abmodel.statespace;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.abmodel.behaviour.AbsBehaviour;

import org.twz.dag.ParameterCore;
import org.twz.statespace.AbsDCore;

import static org.junit.Assert.*;

public class StSpBehaviourFactoryTest {
    private Director Da;
    private AbsDCore DC;

    @Before
    public void setUp() {
        Da = new Director();
        Da.loadBayesNet("src/test/resources/script/pBAD.txt");
        Da.loadDCore("src/test/resources/script/BAD.txt");

        DC = Da.generateDCore("BAD", "pBAD");

        StSpBehaviourFactory.appendResouce("States", DC.getStateSpace());
        StSpBehaviourFactory.appendResouce("Transitions", DC.getTransitionSpace());
    }

    @Test
    public void getReincarnation() {
        JSONObject js = new JSONObject("{'Name': 'life', 'Type': 'Reincarnation', " +
                "'Args': {'s_death': 'Dead', 's_birth': 'Young'}}");
        AbsBehaviour Be = StSpBehaviourFactory.create(js);
        System.out.println(Be.toJSON());
    }

    @Test
    public void getFDShock() {
        JSONObject js = new JSONObject("{'Name': 'foi', 'Type': 'FDShock', " +
                "'Args': {'s_src': 'Middle', 't_tar': 'Die'}}");
        AbsBehaviour Be = StSpBehaviourFactory.create(js);
        System.out.println(Be.toString());
    }
}