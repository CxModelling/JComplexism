package org.twz.cx.element;

import org.junit.Before;
import org.junit.Test;

import java.util.PriorityQueue;
import java.util.Queue;

import static org.junit.Assert.*;

/**
 *
 * Created by TimeWz on 10/08/2018.
 */
public class ScheduleTest {
    class Agent implements Comparable<Agent> {
        private String X;
        int I;

        Agent(String x, int i) {
            X = x;
            I = i;
        }

        @Override
        public int compareTo(Agent o) {
            return (I > o.I)?1:(I == o.I)?0:-1;
        }

        @Override
        public String toString() {
            return "Agent{" +
                    "X='" + X +
                    "', I=" + I +
                    '}';
        }
    }

    @Before
    public void setUp() {

    }

    @Test
    public void getRequests() {
        Agent a1 = new Agent("a", 1), a2 = new Agent("b", 2), a3 = new Agent("c", 3);
        Queue<Agent> q = new PriorityQueue<>();
        q.add(a1);
        q.add(a2);
        q.add(a3);

        for(Agent ag:q){
            System.out.println(ag);
        }

        a2.I = 5;
        q.remove(a2);
        q.add(a2);
        for(Agent ag:q){
            System.out.println(ag);
        }
    }

}