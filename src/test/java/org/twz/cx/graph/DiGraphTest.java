package org.twz.cx.graph;

/**
 *
 * Created by TimeWz on 07/08/2018.
 */

import org.junit.Before;
import org.junit.Test;
import org.twz.graph.DiGraph;
import static org.junit.Assert.*;

public class DiGraphTest {
    private DiGraph<String> DG, DAG;

    @Before
    public void setUp() throws Exception {
        DG = new DiGraph<>();

        DG.addEdge("A", "C");
        DG.addEdge("B", "D");
        DG.addEdge("D", "B");
        DG.addEdge("C", "D");


        DAG = new DiGraph<>();

        DAG.addEdge("A", "B");
        DAG.addEdge("B", "C");
        DAG.addEdge("A", "C");
        DAG.addEdge("C", "D");
    }

    @Test
    public void checkRelation() throws Exception {
        assertArrayEquals(DG.getParents("B").toArray(), new String[]{"D"});
    }

    @Test
    public void getOrder() throws Exception {
        assertArrayEquals(DAG.getOrder().toArray(), new String[]{"A", "B", "C", "D"});
    }

    @Test
    public void checkAcyclic() throws Exception {
        assertTrue(DAG.isAcyclic());
        assertFalse(DG.isAcyclic());
    }
}
