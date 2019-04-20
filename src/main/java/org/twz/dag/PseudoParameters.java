package org.twz.dag;

import java.util.Map;

public class PseudoParameters extends Parameters {
    public PseudoParameters(String nickname, ParameterGroup sg, Map<String, Double> fixed, double prior) {
        super(nickname, sg, fixed, prior);
    }

    public PseudoParameters(String nickname, Map<String, Double> fixed) {
        this(nickname, null, fixed, 0);
    }
}
