package org.twz.cx.abmodel.behaviour.trigger;


import org.twz.cx.mcore.AbsSimModel;



/**
 *
 * Created by TimeWz on 2017/2/14.
 */
public class ForeignTrigger extends Trigger {
    private final String Model, Node;

    public ForeignTrigger(String model, String node) {
        super();
        Model = model;
        Node = node;
    }

    public boolean checkForeign(AbsSimModel model, String node) {
        return model.getName().equals(Model) & node.equals(Node);
    }
}
