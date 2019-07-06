package org.twz.graph;

/*
 *
 * Created by TimeWz on 07/08/2018.
 */

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class DiGraphTest {
    private DiGraph<String> DG, DAG, Graph;

    @Before
    public void setUp() {
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

        Graph = new DiGraph<>();
        Graph.addEdge("A", "B");
        Graph.addEdge("B", "D");
        Graph.addEdge("B", "F");
        Graph.addEdge("C", "F");
        Graph.addEdge("C", "H");
        Graph.addEdge("D", "C");
        Graph.addEdge("D", "E");
        Graph.addEdge("E", "H");
        Graph.addEdge("F", "G");

    }

    @Test
    public void checkRelation() {
        assertArrayEquals(DG.getParents("B").toArray(), new String[]{"D"});
    }

    @Test
    public void getOrder() {
        assertArrayEquals(DAG.getOrder().toArray(), new String[]{"A", "B", "C", "D"});
    }

    @Test
    public void checkAcyclic() {
        assertTrue(DAG.isAcyclic());
        assertFalse(DG.isAcyclic());
    }

    @Test
    public void minimalSub() {
        List<String> cut = new ArrayList<>();
        cut.add("D");
        cut.add("E");
        cut.add("F");

        assertArrayEquals(Graph.getMinimalDAG(cut).getOrder().toArray(), new String[]{"D", "C", "E", "F"});

        List<String> pars = new ArrayList<>();
        pars.add("D");
        assertArrayEquals(Graph.getMediators("H", pars).toArray(), new String[]{"C", "E"});
    }

    @Test
    public void minimalReq() {
        List<String> pars = new ArrayList<>();
        assertArrayEquals(Graph.getMinimalRequirement("H", pars).toArray(), new String[]{"A", "B", "D", "C", "E"});
        pars.add("D");
        assertArrayEquals(Graph.getMinimalRequirement("H", pars).toArray(), new String[]{"D", "C", "E"});
    }
}
