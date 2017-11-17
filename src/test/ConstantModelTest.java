package test;

import hgm.multimodel.ModelSet;
import hgm.multimodel.Y0s;
import junit.framework.TestCase;
import mcore.*;

/**
 *
 * Created by TimeWz on 29/09/2017.
 */
public class ConstantModelTest extends TestCase {
    public void testBuildConstantModel() throws Exception {
        LeafModel Mod = new ConstantModel("A", new Meta("P1", "D1", "K"), 0.3);

        Simulator.simulate(Mod, null, 0, 3, 1, true);
    }

    public void testBuildBranchModel() throws Exception {
        ModelSet Models = new ModelSet("X", new Meta("PX", "DX", "KX"), 1.0);
        Models.append(new ConstantModel("A1", new Meta("P1", "D1", "K"), 0.6));
        Models.append(new ConstantModel("A2", new Meta("P2", "D2", "K"), 0.7));
        Y0s y0 = new Y0s();
        y0.put("A1", new Y0());
        y0.put("A2", new Y0());
        Simulator.simulate(Models, y0, 0, 3, 1, true);
    }
}