package org.twz.dag;

import java.util.Map;

public interface ParameterGenerator {
    Map<String, Object> getParameters(String group, Map<String, Object> exo);
}
