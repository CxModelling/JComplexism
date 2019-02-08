package org.twz.dag;

import java.util.Map;

public class PseudoParameterCore extends ParameterCore {

    public PseudoParameterCore(String nickname, Map<String, Double> fixed) {
        this(nickname, null, fixed, 0);
    }

    public PseudoParameterCore(String nickname, SimulationGroup sg, Map<String, Double> fixed, double prior) {
        super(nickname, sg, fixed, prior);
    }
}
