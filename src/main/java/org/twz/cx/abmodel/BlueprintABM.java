package org.twz.cx.abmodel;

import org.twz.cx.mcore.IMCoreBlueprint;

/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class BlueprintABM implements IMCoreBlueprint{
    private String Name;
    private String TargetPCore;
    private String TargetDCore;

    public BlueprintABM(String name, String pc, String dc) {
        Name = name;
        TargetPCore = pc;
        TargetDCore = dc;
    }

    public String getName() {
        return Name;
    }

    public String getTargetPCore() {
        return TargetPCore;
    }

    public String getTargetDCore() {
        return TargetDCore;
    }
}
