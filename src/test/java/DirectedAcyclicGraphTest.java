import junit.framework.TestCase;
import org.twz.dag.ScriptException;
import org.twz.io.IO;

import java.util.Arrays;

/**
 * Created by TimeWz on 06/11/2017.
 */
public class DirectedAcyclicGraphTest extends TestCase {

    private DirectedAcyclicGraph DAG;

    {
        try {
            DAG = new DirectedAcyclicGraph(IO.loadText("script/pSIR.txt"));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public DirectedAcyclicGraphTest() {
        //System.out.println(DAG.toJSON());
    }


    public void testGetOrder() {
        DAG.print();
        System.out.println(Arrays.toString(DAG.getOrder()));
    }

    public void testGetRoots() {
        System.out.println(DAG.getRoots());
    }

    public void testGetLeaves() {
        System.out.println(DAG.getLeaves());
    }

    public void testGetPathways() {
        System.out.println(DAG.getPathways());
    }

    public void testSample() {
        System.out.println(DAG.sample());
    }

}