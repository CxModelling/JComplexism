package org.twz.dag;

import org.twz.dag.actor.ActorBlueprint;
import org.twz.dag.actor.SimulationActor;
import org.twz.dag.loci.Loci;

import java.util.*;

/**
 *
 * Created by TimeWz on 08/08/2018.
 */
class SimulationGroup {
    private String Name;
    private SimulationCore SC;
    private Set<String> Listening, Waiting, BeFixed, BeRandom, BeActor;
    private List<Loci> FixedChain;
    private List<String> Children;
    private List<ActorBlueprint> Bps;

    public SimulationGroup(String name, Set<String> listening, Set<String> waiting,
                           Set<String> fixed, Set<String> random, Set<String> actor) {
        Name = name;
        Listening = listening;
        Waiting = waiting;
        BeFixed = fixed;
        BeRandom = random;
        BeActor = actor;
        Children = new ArrayList<>();
    }

    public String getName() {
        return Name;
    }

    public void setSimulationCore(SimulationCore sc) {
        SC = sc;
        // todo
    }

    public Map<String, SimulationActor> getActors(Map<String, Double> pas, boolean hoist) {

    }

    public ParameterCore generate(String nickname, Map<String, Double> exo,
                                  ParameterCore parent, boolean actors) {

    }

    public void setResponse(Set<String> imp, Set<String> fixed, Set<String> actors, boolean hoist, ParameterCore pc) {

    }

    public SimulationActor setChildActors(ParameterCore pa, String group) {

    }

    public ParameterCore breed(String nickname, String group, Map<String, Double> exo, ParameterCore parent) {

    }
}
