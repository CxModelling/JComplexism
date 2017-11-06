package test;

import junit.framework.TestCase;
import pcore.DirectedAcyclicGraph;
import pcore.ScriptException;
import sun.font.Script;
import utils.IO;

import java.util.Arrays;

/**
 * Created by TimeWz on 06/11/2017.
 */
public class DirectedAcyclicGraphTest extends TestCase {

    private DirectedAcyclicGraph DAG = new DirectedAcyclicGraph(IO.loadText("script/pSIR.txt"));

    public DirectedAcyclicGraphTest() throws ScriptException {
        //System.out.println(DAG.toJSON());
    }


    public void testGetOrder() throws Exception {
        DAG.print();
        System.out.println(Arrays.toString(DAG.getOrder()));
    }

    public void testGetRoots() throws Exception {
        System.out.println(DAG.getRoots());
    }

    public void testGetLeaves() throws Exception {
        System.out.println(DAG.getLeaves());
    }

    public void testGetPathways() throws Exception {
        System.out.println(DAG.getPathways());
    }

    public void testSample() throws Exception {
        System.out.println(DAG.sample());
    }

}